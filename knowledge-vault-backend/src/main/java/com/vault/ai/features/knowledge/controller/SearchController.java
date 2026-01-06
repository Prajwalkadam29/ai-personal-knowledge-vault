package com.vault.ai.features.knowledge.controller;

import com.vault.ai.features.knowledge.dto.SearchResult;
import com.vault.ai.features.knowledge.service.KnowledgeService;
import com.vault.ai.features.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final KnowledgeService knowledgeService;
    private final NoteRepository noteRepository; // Inject JPA repo

    @GetMapping("/hybrid")
    public ResponseEntity<Map<String, Object>> hybridSearch(@RequestParam String query) {
        var semanticResults = knowledgeService.searchSimilarNotes(query, 5);
        var keywordResults = noteRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);

        Map<String, Object> response = new HashMap<>();
        response.put("semanticResults", semanticResults);
        response.put("keywordResults", keywordResults);

        return ResponseEntity.ok(response);
    }
}