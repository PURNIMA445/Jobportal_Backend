package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.*;
import com.example.Jobportal.enums.NotifType;
import com.example.Jobportal.model.NotificationResponse;
import com.example.Jobportal.repository.*;
import com.example.Jobportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JobRepository jobRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    @Override
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        List<NotificationEntity> notifications = notificationRepository
                .findByUserIdAndIsRead(userId, false);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void sendJobMatchNotifications(Long jobId) {
        // get job and its required skills
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        List<Long> jobSkillIds = job.getRequiredSkills()
                .stream()
                .map(SkillEntity::getId)
                .collect(Collectors.toList());

        if (jobSkillIds.isEmpty()) return;

        // find candidates whose skills overlap with job skills
        List<CandidateProfileEntity> allCandidates =
                candidateProfileRepository.findAll();

        List<CandidateProfileEntity> matchingCandidates = allCandidates.stream()
                .filter(candidate -> candidate.getSkills().stream()
                        .anyMatch(skill -> jobSkillIds.contains(skill.getId())))
                .collect(Collectors.toList());

        // send notification to each matching candidate
        List<NotificationEntity> notifications = matchingCandidates.stream()
                .map(candidate -> NotificationEntity.builder()
                        .user(candidate.getUser())
                        .message("New job matches your skills: " + job.getTitle()
                                + " at " + job.getCompany().getName())
                        .type(NotifType.JOB_MATCH)
                        .jobId(jobId)
                        .isRead(false)
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }

    private NotificationResponse toResponse(NotificationEntity n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.getIsRead())
                .jobId(n.getJobId())
                .createdAt(n.getCreatedAt())
                .build();
    }
}