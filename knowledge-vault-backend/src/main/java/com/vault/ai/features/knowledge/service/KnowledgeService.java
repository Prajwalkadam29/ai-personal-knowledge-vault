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

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final NoteNodeRepository noteNodeRepository;
    private final VectorStore vectorStore;

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
}