package com.example.Jobportal.controller;

import com.example.Jobportal.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/{jobId}")
    public ResponseEntity<?> saveJob(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long jobId) {
        savedJobService.saveJob(userId, jobId);
        return ResponseEntity.ok("Job saved");
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> unsaveJob(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long jobId) {
        savedJobService.unsaveJob(userId, jobId);
        return ResponseEntity.ok("Job removed from saved");
    }

    @GetMapping
    public ResponseEntity<?> getSavedJobs(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(savedJobService.getSavedJobs(userId));
    }
}