package com.example.Jobportal.controller;

import com.example.Jobportal.enums.AppStatus;
import com.example.Jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.Jobportal.dto.MatchScoreResponse;
import com.example.Jobportal.service.AiMatchingService;
import com.example.Jobportal.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
import com.example.Jobportal.entity.ApplicationEntity;
import com.example.Jobportal.entity.JobEntity;
import com.example.Jobportal.repository.ApplicationRepository;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final FileStorageService fileStorageService;


    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> apply(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long jobId,
            @RequestParam(required = false) String coverLetter) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.apply(userId, jobId, coverLetter));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(applicationService.getMyApplications(userId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getJobApplications(
            @PathVariable Long jobId,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(
                applicationService.getJobApplications(jobId, userId));
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Long userId,
            @RequestParam AppStatus status) {
        return ResponseEntity.ok(
                applicationService.updateStatus(applicationId, userId, status));
    }

    @PostMapping("/{applicationId}/check-score")
    public ResponseEntity<?> checkScore(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(applicationService.checkMatchScore(applicationId, userId));
    }

    // NEW — recruiter views/downloads a candidate's CV for an application to THEIR job only
    @GetMapping("/{applicationId}/cv")
    public ResponseEntity<?> getApplicationCv(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Long userId) {
        ApplicationEntity application =
                applicationService.getApplicationForCvAccess(applicationId, userId);

        String resumeUrl = application.getCandidate().getResumeUrl();
        if (resumeUrl == null || resumeUrl.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This candidate has not uploaded a resume");
        }

        Resource resource = fileStorageService.loadResumeAsResource(resumeUrl);

        String extension = resumeUrl.contains(".")
                ? resumeUrl.substring(resumeUrl.lastIndexOf(".")).toLowerCase()
                : "";
        MediaType contentType = switch (extension) {
            case ".pdf" -> MediaType.APPLICATION_PDF;
            case ".doc" -> MediaType.valueOf("application/msword");
            case ".docx" -> MediaType.valueOf(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        String downloadName = application.getCandidate().getFullName()
                .replaceAll("\\s+", "_") + "_resume" + extension;

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + downloadName + "\"")
                .body(resource);
    }
}