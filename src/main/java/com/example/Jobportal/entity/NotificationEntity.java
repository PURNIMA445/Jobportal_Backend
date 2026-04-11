package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── RELATION: Many Notifications → One User ───────────────────────────────
    // Every notification is delivered to a specific user (candidate OR recruiter).
    // FK "user_id" lives in notifications table.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity recipient;

    @Column(nullable = false)
    private String title;                    // Short heading, e.g. "New Match Found!"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;                  // Full notification body

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Builder.Default
    private Boolean isRead = false;

    // ── AI TRIGGER FIELD ──────────────────────────────────────────────────────
    // When the Python AI service computes a matchScore above a threshold (e.g. 0.75),
    // it triggers a notification of type AI_MATCH_ALERT to the candidate.
    // referenceId stores the applicationId or jobId that triggered this notification
    // so the frontend can deep-link directly to the relevant resource.
    private Long referenceId;               // e.g. applicationId, jobId, interviewId
    private String referenceType;           // "APPLICATION", "JOB", "INTERVIEW"

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ── ENUM ──────────────────────────────────────────────────────────────────
    public enum NotificationType {
        // Candidate-facing
        AI_MATCH_ALERT,          // AI found a high-score job match (score > threshold)
        APPLICATION_VIEWED,      // Recruiter opened the candidate's application
        STATUS_CHANGED,          // Application moved to SHORTLISTED / REJECTED / HIRED
        INTERVIEW_SCHEDULED,     // A new interview has been booked
        INTERVIEW_REMINDER,      // Reminder 24h before scheduled interview

        // Recruiter-facing
        NEW_APPLICATION,         // A candidate applied to their job posting
        HIGH_MATCH_CANDIDATE,    // AI flagged a candidate with score > 0.85 for this job

        // System
        SYSTEM_ALERT             // General platform notifications
    }
}