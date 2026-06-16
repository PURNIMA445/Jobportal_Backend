package com.example.Jobportal.controller;

import com.example.Jobportal.enums.AppStatus;
import com.example.Jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> apply(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long jobId,
            @RequestParam(required = false) String coverLetter) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(applicationService.apply(userId, jobId, coverLetter));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(@AuthenticationPrincipal Long userId) {
        try {
            return ResponseEntity.ok(applicationService.getMyApplications(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getJobApplications(
            @PathVariable Long jobId,
            @AuthenticationPrincipal Long userId) {
        try {
            return ResponseEntity.ok(
                    applicationService.getJobApplications(jobId, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Long userId,
            @RequestParam AppStatus status) {
        try {
            return ResponseEntity.ok(
                    applicationService.updateStatus(applicationId, userId, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}