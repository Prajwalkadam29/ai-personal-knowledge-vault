package com.vault.ai.features.auth.service;

import com.vault.ai.features.auth.dto.RegisterRequest;
import com.vault.ai.features.auth.model.User;
import com.vault.ai.features.auth.repository.UserRepository;
import com.vault.ai.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Initializes @Mock and @InjectMocks
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Prajwal", "test@example.com", "password");
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(jwtService.generateToken(any(), any())).thenReturn("mock_jwt_token");

        // Act
        var response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock_jwt_token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class)); // Verify DB was called
    }
}