package com.example.Jobportal.controller;

import com.example.Jobportal.dto.ForgotPasswordRequest;
import com.example.Jobportal.dto.ResetPasswordRequest;
import com.example.Jobportal.dto.ChangePasswordRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.Jobportal.dto.FirebaseAuthRequest;
import com.example.Jobportal.dto.LoginRequest;
import com.example.Jobportal.dto.SignupRequest;
import com.example.Jobportal.model.AuthResponse;
import com.example.Jobportal.model.User;
import com.example.Jobportal.service.AuthService;
import com.example.Jobportal.service.EmailVerificationService;
import com.example.Jobportal.service.FirebaseOAuthService;
import com.example.Jobportal.service.PasswordResetService;
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
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final FirebaseOAuthService firebaseOAuthService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-verification-otp")
    public ResponseEntity<?> sendVerificationOtp(@RequestParam String email) {
        emailVerificationService.sendVerificationOtp(email);
        return ResponseEntity.ok(Map.of("message", "Verification code sent to " + email));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String otp) {
        emailVerificationService.verifyEmail(email, otp);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok("Token is valid, you are authenticated");
    }

    @PostMapping("/firebase")
    public ResponseEntity<?> firebaseLogin(@Valid @RequestBody FirebaseAuthRequest request) {
        return ResponseEntity.ok(
                firebaseOAuthService.loginWithFirebase(request.getIdToken(), request.getRole(), request.isAllowCreate())
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendPasswordResetOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password reset OTP sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(Map.of("message", passwordResetService.resetPassword(request)));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(Map.of("message", passwordResetService.changePassword(userId, request)));
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateAccount(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(Map.of("message", authService.deactivateAccount(userId)));
    }
}