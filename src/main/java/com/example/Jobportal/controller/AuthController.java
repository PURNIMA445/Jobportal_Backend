package com.example.Jobportal.controller;

import com.example.Jobportal.dto.GoogleLoginRequest;
import com.example.Jobportal.dto.LoginRequest;
import com.example.Jobportal.dto.SignupRequest;
import com.example.Jobportal.model.AuthResponse;
import com.example.Jobportal.model.User;
import com.example.Jobportal.service.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            User user = authService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // --- NEW GOOGLE LOGIN ENDPOINT ---
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            // 1. Verify the token securely with Firebase Admin SDK
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getToken());
            String email = decodedToken.getEmail();

            // 2. Pass the verified email and requested role to your AuthService
            AuthResponse response = authService.processSocialLogin(email, request.getRole());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Google Auth Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired Google Token");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok("Token is valid, you are authenticated");
    }
}