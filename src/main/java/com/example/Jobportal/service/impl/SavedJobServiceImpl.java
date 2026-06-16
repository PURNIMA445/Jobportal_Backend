package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.*;
import com.example.Jobportal.model.JobResponse;
import com.example.Jobportal.repository.*;
import com.example.Jobportal.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobRepository jobRepository;
    private final JobServiceImpl jobService;

    @Override
    @Transactional
    public void saveJob(Long userId, Long jobId) {
        CandidateProfileEntity candidate = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        if (savedJobRepository.existsByCandidateIdAndJobId(candidate.getId(), jobId)) {
            throw new RuntimeException("Job already saved");
        }

        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        savedJobRepository.save(SavedJobEntity.builder()
                .candidate(candidate)
                .job(job)
                .build());
    }

    @Override
    @Transactional
    public void unsaveJob(Long userId, Long jobId) {
        CandidateProfileEntity candidate = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));
        savedJobRepository.deleteByCandidateIdAndJobId(candidate.getId(), jobId);
    }

    @Override
    public List<JobResponse> getSavedJobs(Long userId) {
        CandidateProfileEntity candidate = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));
        return savedJobRepository.findByCandidateId(candidate.getId())
                .stream()
                .map(saved -> jobService.toResponse(saved.getJob()))
                .collect(Collectors.toList());
    }
}