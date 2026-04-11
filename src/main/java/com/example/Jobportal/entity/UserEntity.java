package com.example.Jobportal.entity;

import com.example.Jobportal.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ── RELATION: One User ↔ One CandidateProfile ────────────────────────────
    // mappedBy = "user" means CandidateProfileEntity owns the FK column (user_id).
    // CascadeType.ALL: saving/deleting a User auto-saves/deletes their profile.
    // orphanRemoval = true: if profile is unlinked from user, it is deleted from DB.
    // FetchType.LAZY: profile is NOT loaded unless explicitly accessed (performance).
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CandidateProfileEntity candidateProfile;

    // ── RELATION: One User ↔ One RecruiterProfile ────────────────────────────
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RecruiterProfileEntity recruiterProfile;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}