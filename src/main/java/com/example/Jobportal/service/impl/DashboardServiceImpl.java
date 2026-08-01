package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.enums.AppStatus;
import com.example.Jobportal.model.DashboardStatsResponse;
import com.example.Jobportal.repository.ApplicationRepository;
import com.example.Jobportal.repository.CandidateProfileRepository;
import com.example.Jobportal.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ApplicationRepository applicationRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    @Override
    public DashboardStatsResponse getStats(Long userId) {
        CandidateProfileEntity candidate = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        Long candidateId = candidate.getId();

        return DashboardStatsResponse.builder()
                .applications(applicationRepository.countByCandidateId(candidateId))
                .underReview(applicationRepository.countByCandidateIdAndStatus(candidateId, AppStatus.REVIEWED))
                .shortlisted(applicationRepository.countByCandidateIdAndStatus(candidateId, AppStatus.SHORTLISTED))
                .rejected(applicationRepository.countByCandidateIdAndStatus(candidateId, AppStatus.REJECTED))
                .build();
    }
}
