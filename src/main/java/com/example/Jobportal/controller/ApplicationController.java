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
    private final AiMatchingService aiMatchingService;
    private final ApplicationRepository applicationRepository;
    private final ApplicationService applicationService;
    private final FileStorageService fileStorageService;

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

    @PostMapping("/{applicationId}/check-score")
    public ResponseEntity<?> checkScore(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Long userId,
            @RequestParam("resume") MultipartFile resume) {
        try {
            // 1. get the application
            ApplicationEntity application = applicationRepository
                    .findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // 2. verify this candidate owns this application
            if (!application.getCandidate().getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied");
            }

            // 3. get job details for AI
            JobEntity job = application.getJob();
            String requiredSkills = job.getRequiredSkills()
                    .stream()
                    .map(s -> s.getName())
                    .collect(java.util.stream.Collectors.joining(","));

            // 4. call AI service
            MatchScoreResponse aiResult = aiMatchingService.analyzeResume(
                    resume,
                    job.getDescription(),
                    requiredSkills,
                    job.getExperienceLevel().name()
            );

            // 5. store results in application
            application.setMatchScore(aiResult.getMatchScore());
            application.setMissingSkills(
                    String.join(",", aiResult.getMissingSkills())
            );

            // 6. compute rankScore
            int experienceYears = application.getCandidate()
                    .getExperienceYears() != null
                    ? application.getCandidate().getExperienceYears() : 0;

            int projectCount = application.getCandidate()
                    .getProjects() != null
                    ? application.getCandidate().getProjects().size() : 0;

            double expScore = Math.min(experienceYears / 5.0, 1.0) * 20;
            double projectScore = Math.min(projectCount / 3.0, 1.0) * 20;
            double rankScore = (aiResult.getMatchScore() * 0.60)
                    + expScore + projectScore;

            application.setRankScore(Math.round(rankScore * 10.0) / 10.0);

            applicationRepository.save(application);

            // 7. return full AI analysis to frontend
            return ResponseEntity.ok(aiResult);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // NEW — recruiter views/downloads a candidate's CV for an application to THEIR job only
    @GetMapping("/{applicationId}/cv")
    public ResponseEntity<?> getApplicationCv(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal Long userId) {
        try {
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

        } catch (RuntimeException e) {
            if ("Access denied".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            if ("Application not found".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}