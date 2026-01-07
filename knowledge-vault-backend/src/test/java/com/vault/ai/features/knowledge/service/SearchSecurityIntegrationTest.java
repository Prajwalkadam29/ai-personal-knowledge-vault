package com.vault.ai.features.knowledge.service;

import com.vault.ai.features.auth.model.Role;
import com.vault.ai.features.auth.model.User;
import com.vault.ai.features.auth.repository.UserRepository;
import com.vault.ai.features.note.dto.NoteRequest;
import com.vault.ai.features.note.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class SearchSecurityIntegrationTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void userTwoShouldNotSeeUserOneNotesInSearch() {
        // 1. Setup User One and create a private note
        User user1 = createAndLoginUser("user1@test.com", "User One");
        noteService.createNote(new NoteRequest("Secret Java Note", "This is private info about JVM."));

        // 2. Setup User Two
        User user2 = createAndLoginUser("user2@test.com", "User Two");

        // 3. Act: User Two searches for "Java"
        var results = knowledgeService.searchSimilarNotes("Java", 5);

        // 4. Assert: Results must be empty for User Two
        assertTrue(results.isEmpty(), "User Two should not see User One's private notes!");
    }

    private User createAndLoginUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .fullName(name)
                .password("password")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // Set this user as the current authenticated principal
        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return user;
    }
}