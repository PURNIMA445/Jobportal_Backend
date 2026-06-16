package com.example.Jobportal.model;

import com.example.Jobportal.enums.AppStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long candidateId;
    private String candidateName;
    private JobResponse job;
    private AppStatus status;
    private Double matchScore;
    private Double rankScore;
    private String missingSkills;
    private String coverLetter;
    private LocalDateTime appliedAt;
}