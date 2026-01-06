package com.vault.ai.features.ai.dto;

import java.util.List;

public record NoteAnalysisResponse(
        String summary,
        List<String> tags
) {}