package com.vault.ai.security;

import com.vault.ai.features.auth.model.Role;
import com.vault.ai.features.auth.model.User;
import com.vault.ai.features.auth.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 1. Find or Create User in PostgreSQL
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .fullName(name)
                    // Random password for OAuth users since they don't use it
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .build();
            return userRepository.save(newUser);
        });

        // 2. Generate our internal JWT
        String token = jwtService.generateToken(Map.of("fullName", user.getFullName()), user);

        // 3. Redirect user to Flutter app with the token (adjust URL for Flutter later)
        // For now, we redirect to a URL where you can see the token in the browser
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:8080/api/auth/oauth-success?token=" + token);
    }
}