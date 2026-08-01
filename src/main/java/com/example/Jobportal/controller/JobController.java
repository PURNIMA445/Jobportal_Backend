package com.example.Jobportal.controller;

import com.example.Jobportal.dto.JobRequest;
import com.example.Jobportal.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<?> createJob(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllOpenJobs() {
        return ResponseEntity.ok(jobService.getAllOpenJobs());
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<?> getMyJobs(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(@RequestParam String keyword) {
        return ResponseEntity.ok(jobService.searchJobs(keyword));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<?> closeJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(jobService.closeJob(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, userId, request));
    }

    @GetMapping("/recommended")
    public ResponseEntity<?> getRecommendedJobs(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(jobService.getRecommendedJobs(userId));
    }
}