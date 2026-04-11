package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "Java", "Python", "Machine Learning"

    // ── RELATION: Many Skills ↔ Many CandidateProfiles ───────────────────────
    // mappedBy = "skills" means CandidateProfileEntity owns the join table.
    // We use Set to prevent duplicate skill entries per profile.
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<CandidateProfileEntity> candidates = new HashSet<>();

    // ── RELATION: Many Skills ↔ Many Jobs ────────────────────────────────────
    // mappedBy = "requiredSkills" means JobEntity owns the join table.
    @ManyToMany(mappedBy = "requiredSkills", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<JobEntity> jobs = new HashSet<>();
}