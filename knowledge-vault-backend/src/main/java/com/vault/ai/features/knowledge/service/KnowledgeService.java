package com.vault.ai.features.knowledge.service;

import com.vault.ai.features.knowledge.model.NoteNode;
import com.vault.ai.features.knowledge.model.TagNode;
import com.vault.ai.features.knowledge.repository.NoteNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
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
}