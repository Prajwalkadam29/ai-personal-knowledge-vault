package com.vault.ai.features.knowledge.controller;

import com.vault.ai.features.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final KnowledgeService knowledgeService;

    @GetMapping("/topics/{noteId}")
    public ResponseEntity<List<String>> getRecommendedTopics(@PathVariable Long noteId) {
        return ResponseEntity.ok(knowledgeService.getRecommendedTopics(noteId));
    }
}