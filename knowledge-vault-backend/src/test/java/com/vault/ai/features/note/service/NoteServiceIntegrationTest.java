package com.vault.ai.features.note.service;

import com.vault.ai.features.ai.dto.NoteAnalysisResponse;
import com.vault.ai.features.ai.service.AiService;
import com.vault.ai.features.auth.model.Role;
import com.vault.ai.features.auth.model.User;
import com.vault.ai.features.auth.repository.UserRepository;
import com.vault.ai.features.note.dto.NoteRequest;
import com.vault.ai.features.note.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional // Rolls back database changes after each test
class NoteServiceIntegrationTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private AiService aiService; // Mock AI to save API costs

    private User testUser;

    @BeforeEach
    void setUp() {
        // 1. Create and save a test user
        testUser = User.builder()
                .fullName("Test User")
                .email("test.integration@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);

        // 2. Mock the Security Context (simulate a logged-in user)
        var auth = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 3. Mock AI Response
        when(aiService.analyzeNote(anyString(), anyString()))
                .thenReturn(new NoteAnalysisResponse("AI Summary", List.of("Java", "Spring")));
    }

    @Test
    void shouldCreateNoteAndPersistWithCorrectUser() {
        // Act
        NoteRequest request = NoteRequest.builder()
                .title("Integration Title")
                .content("Integration Content")
                .build();
        var savedNote = noteService.createNote(request);

        // Assert Postgres
        assertNotNull(savedNote.getId());
        assertEquals("test.integration@example.com", savedNote.getUser().getEmail());

        var foundInDb = noteRepository.findById(savedNote.getId()).orElseThrow();
        assertEquals("AI Summary", foundInDb.getSummary());
    }
}