package com.example.Jobportal.controller;
import com.example.Jobportal.dto.ForgotPasswordRequest;
import com.example.Jobportal.dto.ResetPasswordRequest;
import com.example.Jobportal.dto.ChangePasswordRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.Jobportal.dto.FirebaseAuthRequest;
import com.example.Jobportal.dto.GoogleLoginRequest;
import com.example.Jobportal.dto.LoginRequest;
import com.example.Jobportal.dto.SignupRequest;
import com.example.Jobportal.exception.EmailNotVerifiedException;
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

import java.util.Map;

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
        } catch (EmailNotVerifiedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage(), "email", e.getEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @PostMapping("/send-verification-otp")
    public ResponseEntity<?> sendVerificationOtp(@RequestParam String email) {
        try {
            authService.sendVerificationOtp(email);
            return ResponseEntity.ok(Map.of("message", "Verification code sent to " + email));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String otp) {
        try {
            authService.verifyEmail(email, otp);
            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok("Token is valid, you are authenticated");
    }
    @PostMapping("/firebase")
    public ResponseEntity<?> firebaseLogin(@Valid @RequestBody FirebaseAuthRequest request) {
        try {
            return ResponseEntity.ok(
                    authService.loginWithFirebase(request.getIdToken(), request.getRole(),request.isAllowCreate())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.sendPasswordResetOtp(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "Password reset OTP sent to your email"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            return ResponseEntity.ok(Map.of("message", authService.resetPassword(request)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            return ResponseEntity.ok(Map.of("message", authService.changePassword(userId, request)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal Long userId) {
        try {
            return ResponseEntity.ok(Map.of("message", authService.deleteAccount(userId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}