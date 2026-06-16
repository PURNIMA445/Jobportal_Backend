package com.example.Jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecruiterProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    private String designation;

    // company ID — recruiter joins existing company
    private Long companyId;
}