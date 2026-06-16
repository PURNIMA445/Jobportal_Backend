package com.example.Jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    private String description;
    private String industry;
    private String location;
    private String websiteUrl;
}