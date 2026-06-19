package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.MatchScoreResponse;
import com.example.Jobportal.service.AiMatchingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AiMatchingServiceImpl implements AiMatchingService {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public MatchScoreResponse analyzeResume(
            MultipartFile resume,
            String jobDescription,
            String requiredSkills,
            String experienceLevel) {

        try {
            // 1. build multipart request body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // wrap file bytes as a resource
            ByteArrayResource fileResource = new ByteArrayResource(resume.getBytes()) {
                @Override
                public String getFilename() {
                    return resume.getOriginalFilename();
                }
            };

            body.add("file", fileResource);
            body.add("jobDescription", jobDescription);
            body.add("requiredSkills", requiredSkills);
            body.add("experienceLevel", experienceLevel);

            // 2. set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // 3. call FastAPI
            ResponseEntity<MatchScoreResponse> response = restTemplate.exchange(
                    aiServiceUrl + "/api/match",
                    HttpMethod.POST,
                    requestEntity,
                    MatchScoreResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("AI service error: " + e.getMessage());
        }
    }
}