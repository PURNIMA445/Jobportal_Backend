package com.example.Jobportal.entity;

import com.example.Jobportal.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;        // null if Google OAuth login

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String googleId;        // null if email/password login

    @Column(nullable = false)
    private Boolean isEmailVerified = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}