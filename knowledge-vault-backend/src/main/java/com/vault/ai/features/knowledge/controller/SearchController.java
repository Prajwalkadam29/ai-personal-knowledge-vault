package com.vault.ai.features.knowledge.controller;

import com.vault.ai.features.knowledge.dto.SearchResult;
import com.vault.ai.features.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final KnowledgeService knowledgeService;

    @GetMapping
    public ResponseEntity<List<SearchResult>> search(@RequestParam String query) {
        return ResponseEntity.ok(knowledgeService.searchSimilarNotes(query, 5));
    }
}