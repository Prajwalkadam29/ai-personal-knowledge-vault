package com.vault.ai.features.auth.controller;

import com.vault.ai.features.auth.dto.AuthenticationResponse;
import com.vault.ai.features.auth.dto.RegisterRequest;
import com.vault.ai.features.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/oauth-success")
    public String oauthSuccess(@RequestParam String token) {
        return "Google Login Successful! Your JWT Token is: " + token;
    }
}