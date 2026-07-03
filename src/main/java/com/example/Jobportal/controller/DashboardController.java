package com.example.Jobportal.controller;

import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.enums.AppStatus;
import com.example.Jobportal.model.DashboardStatsResponse;
import com.example.Jobportal.repository.ApplicationRepository;
import com.example.Jobportal.repository.CandidateProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ApplicationRepository applicationRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    @GetMapping
    public ResponseEntity<?> getStats(@AuthenticationPrincipal Long userId) {
        CandidateProfileEntity candidate = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Long candidateId = candidate.getId();

        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .applications(applicationRepository.countByCandidateId(candidateId))
                .underReview(applicationRepository.countByCandidateIdAndStatus(candidateId, AppStatus.REVIEWED))
                .shortlisted(applicationRepository.countByCandidateIdAndStatus(candidateId, AppStatus.SHORTLISTED))
                .rejected(applicationRepository.countByCandidateIdAndStatus(candidateId, AppStatus.REJECTED))
                .build();

        return ResponseEntity.ok(stats);
    }
}