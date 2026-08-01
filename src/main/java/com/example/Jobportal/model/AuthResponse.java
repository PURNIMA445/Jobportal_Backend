package com.example.Jobportal.model;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String role;
    private Long userId;
    private boolean profileCreated;

    public AuthResponse(String token, String role, Long userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.profileCreated = false;
    }

    public AuthResponse(String token, String role, Long userId, boolean profileCreated) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.profileCreated = profileCreated;
    }
}