package com.example.Jobportal.controller;

import com.example.Jobportal.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateResumeController {

    private final CandidateProfileService candidateProfileService;

    @PostMapping("/resume")
    public ResponseEntity<?> uploadResume(
            @AuthenticationPrincipal Long userId,
            @RequestParam("resume") MultipartFile resume) {
        String storedFilename = candidateProfileService.uploadResume(userId, resume);
        return ResponseEntity.ok(Map.of(
                "message", "Resume uploaded successfully",
                "resumeUrl", storedFilename
        ));
    }
}