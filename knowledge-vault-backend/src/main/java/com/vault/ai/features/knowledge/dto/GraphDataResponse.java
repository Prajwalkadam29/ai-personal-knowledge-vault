package com.vault.ai.features.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class GraphDataResponse {
    private List<NodeDto> nodes;
    private List<EdgeDto> edges;

    @Data
    @Builder
    @AllArgsConstructor
    public static class NodeDto {
        private String id;       // Unique ID for the graph
        private String label;    // Display text (Title or Tag Name)
        private String type;     // "NOTE" or "TAG"
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class EdgeDto {
        private String source;
        private String target;
    }
}