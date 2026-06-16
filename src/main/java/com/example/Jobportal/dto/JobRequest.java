package com.example.Jobportal.dto;

import com.example.Jobportal.enums.ExperienceLevel;
import com.example.Jobportal.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class JobRequest {

    @NotBlank(message = "JobResponse title is required")
    private String title;

    @NotBlank(message = "JobResponse description is required")
    private String description;

    private String location;

    @NotNull(message = "JobResponse type is required")
    private JobType jobType;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    private Double salaryMin;
    private Double salaryMax;

    @NotNull(message = "Company is required")
    private Long companyId;

    private List<Long> requiredSkillIds;
}