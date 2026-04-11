package com.example.Jobportal.entity;

import com.example.Jobportal.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "applications")
public class ApplicationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfileEntity candidate;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobEntity job;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    // AI match score between candidate profile and job description
    private Double matchScore;

    private LocalDate appliedDate;

    public ApplicationEntity() {}

    public ApplicationEntity(CandidateProfileEntity candidate, JobEntity job, ApplicationStatus status, Double matchScore, LocalDate appliedDate) {
        this.candidate = candidate;
        this.job = job;
        this.status = status;
        this.matchScore = matchScore;
        this.appliedDate = appliedDate;
    }
}
