package com.vault.ai.features.note.service;

import com.vault.ai.features.note.dto.NoteRequest;
import com.vault.ai.features.note.model.Note;
import com.vault.ai.features.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.vault.ai.features.ai.service.AiService;
import com.vault.ai.features.ai.dto.NoteAnalysisResponse;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final AiService aiService;

    @Transactional
    public Note createNote(NoteRequest request) {
        // 1. Create initial Note
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        // 2. AI Orchestration: Get Summary and Tags
        try {
            NoteAnalysisResponse analysis = aiService.analyzeNote(note.getTitle(), note.getContent());
            note.setSummary(analysis.summary());
            // Note: We will handle the "Tags" in the Graph Module (Neo4j) next!
        } catch (Exception e) {
            // Log error but allow note creation to succeed without AI if Groq is down
            System.err.println("AI Analysis failed: " + e.getMessage());
        }

        return noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
    }
}