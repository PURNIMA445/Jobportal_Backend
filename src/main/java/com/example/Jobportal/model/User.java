package com.example.Jobportal.model;

import com.example.Jobportal.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private Role role;
    private Boolean isEmailVerified;
}