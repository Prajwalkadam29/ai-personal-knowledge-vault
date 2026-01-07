package com.vault.ai.features.knowledge.service;

import com.vault.ai.features.auth.model.User;
import com.vault.ai.features.knowledge.dto.SearchResult;
import com.vault.ai.features.knowledge.model.NoteNode;
import com.vault.ai.features.knowledge.model.TagNode;
import com.vault.ai.features.knowledge.repository.NoteNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vault.ai.features.knowledge.dto.GraphDataResponse;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final NoteNodeRepository noteNodeRepository;
    private final VectorStore vectorStore;
    private final Driver neo4jDriver;

    // Update syncNoteToGraph signature
    @Transactional("neo4jTransactionManager")
    public void syncNoteToGraph(Long id, Long userId, String title, String content, List<String> tags) {

        // 1. CONCEPT LINKING: Only search for similar notes BELONGING TO THIS USER
        SearchRequest linkRequest = SearchRequest.builder()
                .query(content)
                .topK(3)
                .similarityThreshold(0.7)
                .filterExpression("userId == " + userId) // CRITICAL: Security Filter
                .build();

        List<Document> similarDocs = vectorStore.similaritySearch(linkRequest);

        Set<NoteNode> existingLinks = similarDocs.stream()
                .map(doc -> doc.getMetadata().get("noteId"))
                .filter(noteId -> noteId != null && !noteId.equals(id))
                .map(noteId -> noteNodeRepository.findById(((Number) noteId).longValue()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        // 2. SAVE NOTE NODE WITH USER ID
        NoteNode node = NoteNode.builder()
                .noteId(id)
                .userId(userId) // Set the owner
                .title(title)
                .tags(tags.stream()
                        .map(name -> TagNode.builder().name(name).build())
                        .collect(Collectors.toSet()))
                .relatedNotes(existingLinks)
                .build();

        noteNodeRepository.save(node);

        // 3. VECTOR STORE: Store userId in metadata for future filtering
        Document doc = new Document(content, Map.of(
                "noteId", id,
                "userId", userId, // CRITICAL: Metadata for filtering
                "title", title
        ));
        vectorStore.add(List.of(doc));
    }

    // Update Search logic
    public List<SearchResult> searchSimilarNotes(String query, int topK) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 1. Correct builder pattern for Spring AI 1.1.2
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(0.5)
                .filterExpression("userId == " + currentUser.getId()) // SECURE SEARCH
                .build();

        // 2. Execute search
        List<Document> results = vectorStore.similaritySearch(searchRequest);

        // 3. Map metadata back to DTO
        return results.stream()
                .map(doc -> SearchResult.builder()
                        .noteId(((Number) doc.getMetadata().get("noteId")).longValue())
                        .title((String) doc.getMetadata().get("title"))
                        // Spring AI 1.1.2 stores distance/score in metadata
                        .similarityScore(1.0)
                        .build())
                .collect(Collectors.toList());
    }

    public GraphDataResponse getFullGraph() {
        // 1. Get the current user from Security Context
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Long currentUserId = currentUser.getId();

        List<GraphDataResponse.NodeDto> nodes = new ArrayList<>();
        List<GraphDataResponse.EdgeDto> edges = new ArrayList<>();

        try (Session session = neo4jDriver.session()) {
            // 2. UPDATED CYPHER: Added WHERE n.userId = $userId
            String cypher = """
            MATCH (n:Note)
            WHERE n.userId = $userId
            OPTIONAL MATCH (n)-[:HAS_TAG]->(t:Tag)
            RETURN n.noteId AS noteId, n.title AS title, t.name AS tagName
            """;

            session.run(cypher, Map.of("userId", currentUserId)).list().forEach(record -> {
                var noteIdValue = record.get("noteId");
                if (!noteIdValue.isNull()) {
                    String noteIdStr = "note_" + noteIdValue.asLong();

                    if (nodes.stream().noneMatch(n -> n.getId().equals(noteIdStr))) {
                        nodes.add(new GraphDataResponse.NodeDto(noteIdStr, record.get("title").asString(), "NOTE"));
                    }

                    var tagValue = record.get("tagName");
                    if (!tagValue.isNull()) {
                        String tagId = "tag_" + tagValue.asString();
                        if (nodes.stream().noneMatch(n -> n.getId().equals(tagId))) {
                            nodes.add(new GraphDataResponse.NodeDto(tagId, tagValue.asString(), "TAG"));
                        }
                        edges.add(new GraphDataResponse.EdgeDto(noteIdStr, tagId));
                    }
                }
            });
        }
        return new GraphDataResponse(nodes, edges);
    }

    public List<String> getRecommendedTopics(Long noteId) {
        // This calls the Cypher query we wrote in the Repository
        return noteNodeRepository.suggestRelatedTopics(noteId);
    }
}