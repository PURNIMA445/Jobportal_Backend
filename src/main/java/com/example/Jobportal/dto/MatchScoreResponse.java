package com.example.Jobportal.dto;

import lombok.Data;
import java.util.List;

@Data
public class MatchScoreResponse {
    private Double matchScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String experienceMatch;
    private String projectRelevance;
    private List<String> suggestions;
}