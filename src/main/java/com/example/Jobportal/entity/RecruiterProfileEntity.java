package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recruiter_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── RELATION: One RecruiterProfile ↔ One User ────────────────────────────
    // FK column "user_id" lives in recruiter_profiles table (this side owns it).
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    // ── RELATION: Many RecruiterProfiles → One Company ───────────────────────
    // Many recruiters can belong to the same company.
    // FK column "company_id" lives in recruiter_profiles table.
    // FetchType.LAZY: company details only loaded when explicitly needed.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    private String designation;   // e.g. "HR Manager", "Technical Recruiter"
    private String phoneNumber;
    private String linkedinUrl;
}