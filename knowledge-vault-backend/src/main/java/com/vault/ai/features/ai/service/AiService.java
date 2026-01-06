package com.vault.ai.features.ai.service;

import com.vault.ai.features.ai.dto.NoteAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;

    public NoteAnalysisResponse analyzeNote(String title, String content) {
        return chatClient.prompt()
                .system(s -> s.text("""
                    You are a technical assistant for a Knowledge Vault app.
                    Analyze the provided note and return a JSON object with:
                    1. 'summary': A concise 2-sentence summary.
                    2. 'tags': A list of technical tags/keywords.
                    Return ONLY valid JSON.
                    """))
                .user(u -> u.text("Title: " + title + "\nContent: " + content))
                .call()
                .entity(NoteAnalysisResponse.class);
    }
}