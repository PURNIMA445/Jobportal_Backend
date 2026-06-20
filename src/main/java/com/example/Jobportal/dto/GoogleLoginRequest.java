package com.example.Jobportal.dto;

import com.example.Jobportal.enums.Role;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String token;
    private Role role; 
}