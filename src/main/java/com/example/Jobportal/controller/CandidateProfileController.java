package com.example.Jobportal.controller;

import com.example.Jobportal.dto.CandidateProfileRequest;
import com.example.Jobportal.model.CandidateProfileResponse;
import com.example.Jobportal.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CandidateProfileRequest request) {
        try {
            CandidateProfileResponse response =
                    candidateProfileService.createProfile(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Long userId) {
        try {
            return ResponseEntity.ok(candidateProfileService.getProfile(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CandidateProfileRequest request) {
        try {
            return ResponseEntity.ok(
                    candidateProfileService.updateProfile(userId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Long getUserIdFromEmail(String email) {
        // we'll replace this with a cleaner solution next
        throw new RuntimeException("Not implemented yet");
    }
}