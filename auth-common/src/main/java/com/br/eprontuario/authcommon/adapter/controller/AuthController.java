package com.br.eprontuario.authcommon.adapter.controller;

import com.br.eprontuario.authcommon.adapter.config.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    // Simple in-memory user store for demo (replace with database in production)
    private static final Map<String, String> USERS = new HashMap<>();
    static {
        // password: password123
        USERS.put("doctor1", "$2a$12$jlqJJ7YjJQVuJYEQQKSh1uyVsXZbXuGlOJIJOcNsblzK5GpFYoEPe");
        USERS.put("nurse1", "$2a$12$jlqJJ7YjJQVuJYEQQKSh1uyVsXZbXuGlOJIJOcNsblzK5GpFYoEPe");
        USERS.put("patient1", "$2a$12$jlqJJ7YjJQVuJYEQQKSh1uyVsXZbXuGlOJIJOcNsblzK5GpFYoEPe");
    }

    private static final Map<String, Set<String>> USER_ROLES = new HashMap<>();
    static {
        USER_ROLES.put("doctor1", Set.of("DOCTOR"));
        USER_ROLES.put("nurse1", Set.of("NURSE"));
        USER_ROLES.put("patient1", Set.of("PATIENT"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        if (!USERS.containsKey(request.getUsername())) {
            log.warn("Login failed: user not found - {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String storedHash = USERS.get(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), storedHash)) {
            log.warn("Login failed: invalid password for user - {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.nameUUIDFromBytes(request.getUsername().getBytes());
        Set<String> roles = USER_ROLES.getOrDefault(request.getUsername(), Set.of());

        String token = tokenProvider.generateToken(userId, request.getUsername(), 
                request.getUsername() + "@hospital.local", roles);

        log.info("Login successful for user: {} with roles: {}", request.getUsername(), roles);

        return ResponseEntity.ok(new LoginResponse(token, userId.toString(), request.getUsername(), new ArrayList<>(roles)));
    }

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(String token, String userId, String username, List<String> roles) {}
}
