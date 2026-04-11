package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── RELATION: One CandidateProfile ↔ One User ────────────────────────────
    // @JoinColumn defines the FK column "user_id" in the candidate_profiles table.
    // This side OWNS the relationship (has the FK), so no mappedBy here.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    // ── AI-READY FIELDS ───────────────────────────────────────────────────────
    // These TEXT fields are the primary input for the AI/Transformer matching engine.
    // resumeText: raw parsed resume content → used to generate sentence embeddings.
    // summary: candidate-written bio → used as a secondary semantic signal.
    // yearsOfExperience: numeric filter applied BEFORE AI matching (pre-filter step).
    @Column(columnDefinition = "TEXT")
    private String resumeText;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private Integer yearsOfExperience;

    private String location;
    private String phoneNumber;
    private String linkedinUrl;
    private String portfolioUrl;

    // ── RELATION: Many Candidates ↔ Many Skills ───────────────────────────────
    // This side OWNS the join table "candidate_skills" with columns (candidate_id, skill_id).
    // Skills are loaded LAZILY — only fetched when the AI engine needs them for matching.
    // cascade = MERGE, PERSIST: adding a new skill to a candidate persists it automatically.
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "candidate_skills",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<SkillEntity> skills = new HashSet<>();

    // ── RELATION: One Candidate → Many Applications ───────────────────────────
    // mappedBy = "candidate" means ApplicationEntity owns the FK.
    // CascadeType.ALL: deleting a candidate removes all their applications.
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ApplicationEntity> applications = new HashSet<>();

    // ── HELPER METHODS ────────────────────────────────────────────────────────
    // Bidirectional sync helpers keep both sides of M2M consistent in memory.
    public void addSkill(SkillEntity skill) {
        this.skills.add(skill);
        skill.getCandidates().add(this);
    }

    public void removeSkill(SkillEntity skill) {
        this.skills.remove(skill);
        skill.getCandidates().remove(this);
    }
}