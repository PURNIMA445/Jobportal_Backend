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
import java.util.Map;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CandidateProfileRequest request) {
        CandidateProfileResponse response =
                candidateProfileService.createProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Long userId) {
        try {
            return ResponseEntity.ok(candidateProfileService.getProfile(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CandidateProfileRequest request) {
        return ResponseEntity.ok(
                candidateProfileService.updateProfile(userId, request));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCandidates() {
        return ResponseEntity.ok(candidateProfileService.getAllProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCandidateById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(candidateProfileService.getProfileById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}