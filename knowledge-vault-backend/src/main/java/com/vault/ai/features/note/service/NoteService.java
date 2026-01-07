package com.vault.ai.features.note.service;

import com.vault.ai.features.knowledge.service.KnowledgeService;
import com.vault.ai.features.note.dto.NoteRequest;
import com.vault.ai.features.note.model.Note;
import com.vault.ai.features.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.vault.ai.features.ai.service.AiService;
import com.vault.ai.features.ai.dto.NoteAnalysisResponse;

import com.vault.ai.features.auth.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final AiService aiService;
    private final KnowledgeService knowledgeService;

    // Explicitly use the JPA transaction manager (usually named "transactionManager")
    @Transactional("transactionManager")
    public Note createNote(NoteRequest request) {
        // 1. EXTRACT CURRENT USER FROM SECURITY CONTEXT
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // 2. BUILD THE NOTE WITH USER OWNERSHIP
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(currentUser) // Stamped with owner
                .build();

        NoteAnalysisResponse analysis = null;
        try {
            analysis = aiService.analyzeNote(note.getTitle(), note.getContent());
            note.setSummary(analysis.summary());
        } catch (Exception e) {
            System.err.println("AI Analysis failed: " + e.getMessage());
        }

        Note savedNote = noteRepository.save(note);

        // 3. SYNC TO GRAPH WITH USER ID
        if (analysis != null) {
            knowledgeService.syncNoteToGraph(
                    savedNote.getId(),
                    currentUser.getId(), // NEW PARAMETER
                    savedNote.getTitle(),
                    savedNote.getContent(),
                    analysis.tags()
            );
        }

        return savedNote;
    }

    // UPDATE: Users should only see their own notes
    public List<Note> getAllNotes() {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        // You'll need to add findByUser to NoteRepository
        return noteRepository.findByUser(currentUser);
    }

    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
    }
}