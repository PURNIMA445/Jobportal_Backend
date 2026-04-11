package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "saved_jobs",
        uniqueConstraints = {
                // A candidate can save the same job only once
                @UniqueConstraint(columnNames = {"candidate_id", "job_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── RELATION: Many SavedJobs → One CandidateProfile ──────────────────────
    // FK "candidate_id" lives in saved_jobs table.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfileEntity candidate;

    // ── RELATION: Many SavedJobs → One Job ───────────────────────────────────
    // FK "job_id" lives in saved_jobs table.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobEntity job;

    // ── AI CONTEXT FIELD ──────────────────────────────────────────────────────
    // When a candidate saves a job, we store the matchScore at the time of saving.
    // This lets the frontend show "85% match" on the saved jobs list even if
    // the AI hasn't re-scored it recently, and helps train future recommendation models
    // (saved jobs with high interaction = implicit positive signal for the AI).
    private Double matchScoreAtSave;

    @Column(nullable = false, updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onCreate() {
        this.savedAt = LocalDateTime.now();
    }
}