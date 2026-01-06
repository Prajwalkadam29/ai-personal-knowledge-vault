package com.vault.ai.features.knowledge.service;

import com.vault.ai.features.knowledge.dto.SearchResult;
import com.vault.ai.features.knowledge.model.NoteNode;
import com.vault.ai.features.knowledge.model.TagNode;
import com.vault.ai.features.knowledge.repository.NoteNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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

    @Transactional("neo4jTransactionManager")
    public void syncNoteToGraph(Long id, String title, String content, List<String> tags) {
        // 1. Save to Knowledge Graph (Neo4j)
        NoteNode node = NoteNode.builder()
                .noteId(id)
                .title(title)
                .tags(tags.stream()
                        .map(name -> TagNode.builder().name(name).build())
                        .collect(Collectors.toSet()))
                .build();
        noteNodeRepository.save(node);

        // 2. Save to Vector Store (Neo4j Vector Index)
        Document doc = new Document(content, Map.of(
                "noteId", id,
                "title", title
        ));
        vectorStore.add(List.of(doc));
    }

    public List<SearchResult> searchSimilarNotes(String query, int topK) {
        // 1. Correct builder pattern for Spring AI 1.1.2
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(0.5) // Adjust precision (0.0 to 1.0)
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
        List<GraphDataResponse.NodeDto> nodes = new ArrayList<>();
        List<GraphDataResponse.EdgeDto> edges = new ArrayList<>();

        try (Session session = neo4jDriver.session()) {
            // Cypher query to get all Notes, all Tags, and the relationships
            String cypher = """
            MATCH (n:Note)
            OPTIONAL MATCH (n)-[:HAS_TAG]->(t:Tag)
            RETURN n.noteId AS noteId, n.title AS title, t.name AS tagName
            """;

            session.run(cypher).list().forEach(record -> {
                // SAFE EXTRACTION: Check if noteId is null before converting to long
                var noteIdValue = record.get("noteId");
                if (!noteIdValue.isNull()) {
                    String noteIdStr = "note_" + noteIdValue.asLong();

                    // Add Note node if not already present
                    if (nodes.stream().noneMatch(n -> n.getId().equals(noteIdStr))) {
                        nodes.add(new GraphDataResponse.NodeDto(noteIdStr, record.get("title").asString(), "NOTE"));
                    }

                    // SAFE EXTRACTION: Check if tag exists
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
}