package com.example.Jobportal.service;

import com.example.Jobportal.dto.MatchScoreResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AiMatchingService {
    MatchScoreResponse analyzeResume(
            MultipartFile resume,
            String jobDescription,
            String requiredSkills,
            String experienceLevel
    );
}