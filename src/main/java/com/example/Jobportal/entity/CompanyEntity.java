package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;  // AI can use this to contextualize job postings

    private String website;
    private String location;
    private String logoUrl;

    // ── RELATION: One Company → Many RecruiterProfiles ───────────────────────
    // A company can have multiple recruiters posting jobs on its behalf.
    // mappedBy = "company" means RecruiterProfileEntity owns the FK column.
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecruiterProfileEntity> recruiters = new ArrayList<>();

    // ── RELATION: One Company → Many Jobs ────────────────────────────────────
    // A company can post multiple jobs. Deleting a company deletes all its jobs.
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JobEntity> jobs = new ArrayList<>();
}