package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // ── AI-READY FIELD ────────────────────────────────────────────────────────
    // description is the PRIMARY TEXT used for AI semantic matching.
    // The Transformer model will encode this into a vector embedding,
    // which is then compared against the candidate's resumeText embedding
    // using cosine similarity to produce the ApplicationEntity.matchScore.
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String location;
    private String jobType;        // FULL_TIME, PART_TIME, CONTRACT, REMOTE

    private Double salaryMin;
    private Double salaryMax;

    private Integer experienceRequired; // used as a pre-filter before AI matching

    private Boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime postedAt;

    private LocalDateTime deadline;

    // ── RELATION: Many Jobs → One Company ────────────────────────────────────
    // FK "company_id" lives in jobs table. Lazy-loaded for performance.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    // ── RELATION: Many Jobs → One RecruiterProfile ───────────────────────────
    // Tracks which recruiter posted this job (for audit + permission checks).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private RecruiterProfileEntity postedBy;

    // ── RELATION: Many Jobs ↔ Many Skills ────────────────────────────────────
    // This side OWNS the join table "job_required_skills".
    // These skills are used as a FAST PRE-FILTER before running the AI model:
    // candidates missing required skills can be filtered out cheaply via SQL,
    // saving expensive embedding computation for well-matched candidates only.
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "job_required_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<SkillEntity> requiredSkills = new HashSet<>();

    // ── RELATION: One Job → Many Applications ────────────────────────────────
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApplicationEntity> applications = new ArrayList<>();

    // ── HELPER METHODS ────────────────────────────────────────────────────────
    public void addSkill(SkillEntity skill) {
        this.requiredSkills.add(skill);
        skill.getJobs().add(this);
    }

    public void removeSkill(SkillEntity skill) {
        this.requiredSkills.remove(skill);
        skill.getJobs().remove(this);
    }

    @PrePersist
    protected void onCreate() {
        this.postedAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
    }
}