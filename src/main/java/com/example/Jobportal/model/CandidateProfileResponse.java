package com.example.Jobportal.model;

import com.example.Jobportal.enums.ProjectComplexity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String location;
    private String bio;
    private String resumeUrl;
    private String profilePicUrl;
    private Integer experienceYears;
    private List<SkillResponse> skills;
    private List<ProjectResponse> projects;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillResponse {
        private Long id;
        private String name;
        private String category;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectResponse {
        private Long id;
        private String title;
        private String description;
        private String techStack;
        private String projectUrl;
        private ProjectComplexity complexity;
    }
}