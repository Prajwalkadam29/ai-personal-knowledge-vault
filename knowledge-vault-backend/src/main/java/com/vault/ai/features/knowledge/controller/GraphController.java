package com.vault.ai.features.knowledge.controller;

import com.vault.ai.features.knowledge.dto.GraphDataResponse;
import com.vault.ai.features.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphController {

    private final KnowledgeService knowledgeService;

    @GetMapping
    public ResponseEntity<GraphDataResponse> getGraph() {
        return ResponseEntity.ok(knowledgeService.getFullGraph());
    }
}