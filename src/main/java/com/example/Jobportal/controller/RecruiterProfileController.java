package com.example.Jobportal.controller;

import com.example.Jobportal.dto.RecruiterProfileRequest;
import com.example.Jobportal.service.RecruiterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody RecruiterProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recruiterProfileService.createProfile(userId, request));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(recruiterProfileService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody RecruiterProfileRequest request) {
        return ResponseEntity.ok(
                recruiterProfileService.updateProfile(userId, request));
    }
}