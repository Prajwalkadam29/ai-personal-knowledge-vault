package com.vault.ai.features.knowledge.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResult {
    private Long noteId;
    private String title;
    private double similarityScore;
}