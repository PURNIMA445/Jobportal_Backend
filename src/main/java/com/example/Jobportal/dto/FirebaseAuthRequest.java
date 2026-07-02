package com.example.Jobportal.dto;

import com.example.Jobportal.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FirebaseAuthRequest {

    @NotBlank(message = "Firebase ID token is required")
    private String idToken;

    @NotNull(message = "Role is required")
    private Role role;
    private boolean allowCreate;
}