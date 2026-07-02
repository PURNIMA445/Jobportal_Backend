package com.example.Jobportal.controller;

import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.repository.CandidateProfileRepository;
import com.example.Jobportal.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateResumeController {

    private final CandidateProfileRepository candidateProfileRepository;
    private final FileStorageService fileStorageService;

    @PostMapping("/resume")
    @Transactional
    public ResponseEntity<?> uploadResume(
            @AuthenticationPrincipal Long userId,
            @RequestParam("resume") MultipartFile resume) {
        try {
            CandidateProfileEntity candidate = candidateProfileRepository
                    .findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

            String storedFilename = fileStorageService.storeResume(resume, candidate.getId());
            candidate.setResumeUrl(storedFilename);
            candidateProfileRepository.save(candidate);

            return ResponseEntity.ok(Map.of(
                    "message", "Resume uploaded successfully",
                    "resumeUrl", storedFilename
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}