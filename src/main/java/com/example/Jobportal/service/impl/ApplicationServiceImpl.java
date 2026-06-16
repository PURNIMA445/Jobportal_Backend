package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.*;
import com.example.Jobportal.enums.AppStatus;
import com.example.Jobportal.enums.NotifType;
import com.example.Jobportal.model.ApplicationResponse;
import com.example.Jobportal.repository.*;
import com.example.Jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobRepository jobRepository;
    private final NotificationRepository notificationRepository;
    private final JobServiceImpl jobService;

    @Override
    @Transactional
    public ApplicationResponse apply(Long userId, Long jobId, String coverLetter) {

        CandidateProfileEntity candidate = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByCandidateIdAndJobId(
                candidate.getId(), jobId)) {
            throw new RuntimeException("You have already applied to this job");
        }

        if (job.getStatus().name().equals("CLOSED")) {
            throw new RuntimeException("This job is no longer accepting applications");
        }

        ApplicationEntity application = ApplicationEntity.builder()
                .candidate(candidate)
                .job(job)
                .coverLetter(coverLetter)
                .build();

        ApplicationEntity saved = applicationRepository.save(application);

        // notify recruiter
        notificationRepository.save(NotificationEntity.builder()
                .user(job.getRecruiter().getUser())
                .message(candidate.getFullName() + " applied to your job: "
                        + job.getTitle())
                .type(NotifType.APPLICATION_UPDATE)
                .jobId(jobId)
                .isRead(false)
                .build());

        return toResponse(saved);
    }

    @Override
    public List<ApplicationResponse> getMyApplications(Long userId) {
        CandidateProfileEntity candidate = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        return applicationRepository.findByCandidateId(candidate.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getJobApplications(Long jobId, Long userId) {
        // verify recruiter owns this job
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // return ranked by rankScore
        return applicationRepository.findByJobIdOrderByRankScoreDesc(jobId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(Long applicationId,
                                            Long userId, AppStatus status) {
        ApplicationEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJob().getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        application.setStatus(status);
        ApplicationEntity saved = applicationRepository.save(application);

        // notify candidate of status change
        notificationRepository.save(NotificationEntity.builder()
                .user(application.getCandidate().getUser())
                .message("Your application for " + application.getJob().getTitle()
                        + " has been " + status.name().toLowerCase())
                .type(NotifType.APPLICATION_UPDATE)
                .jobId(application.getJob().getId())
                .isRead(false)
                .build());

        return toResponse(saved);
    }

    private ApplicationResponse toResponse(ApplicationEntity app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .candidateId(app.getCandidate().getId())
                .candidateName(app.getCandidate().getFullName())
                .job(jobService.toResponse(app.getJob()))
                .status(app.getStatus())
                .matchScore(app.getMatchScore())
                .rankScore(app.getRankScore())
                .missingSkills(app.getMissingSkills())
                .coverLetter(app.getCoverLetter())
                .appliedAt(app.getAppliedAt())
                .build();
    }
}