package com.example.Jobportal.model;

import com.example.Jobportal.enums.Role;

// ── WHAT CHANGED ─────────────────────────────────────────────────────────────
// Old: firstName, lastName, emailId  (matched the OLD flat UserEntity)
// New: fullName, email, role         (matches the NEW UserEntity with @Enumerated Role)
// password is intentionally EXCLUDED from this response model (never send it to client)
// ─────────────────────────────────────────────────────────────────────────────
public class User {

    private Long id;
    private String fullName;
    private String email;
    private Role role;

    public User() {}

    public User(Long id, String fullName, String email, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}