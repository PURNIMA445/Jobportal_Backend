package com.example.Jobportal.dto;

import com.example.Jobportal.enums.ProjectComplexity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CandidateProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    private String location;

    private String bio;

    private Integer experienceYears;

    // list of skill IDs the candidate selects
    private List<Long> skillIds;

    // list of projects
    private List<ProjectRequest> projects;

    @Data
    public static class ProjectRequest {
        @NotBlank(message = "Project title is required")
        private String title;

        private String description;
        private String techStack;
        private String projectUrl;

        @NotNull(message = "Complexity is required")
        private ProjectComplexity complexity;
    }
}