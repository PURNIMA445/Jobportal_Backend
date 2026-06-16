package com.example.Jobportal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String designation;
    private CompanyResponse company;
}