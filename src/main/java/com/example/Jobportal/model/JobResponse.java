package com.example.Jobportal.model;

import com.example.Jobportal.enums.ExperienceLevel;
import com.example.Jobportal.enums.JobStatus;
import com.example.Jobportal.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private Double salaryMin;
    private Double salaryMax;
    private JobStatus status;
    private CompanyResponse company;
    private String recruiterName;
    private List<CandidateProfileResponse.SkillResponse> requiredSkills;
    private LocalDateTime createdAt;
}