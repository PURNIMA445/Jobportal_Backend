package com.example.Jobportal.service;

import com.example.Jobportal.dto.RecruiterProfileRequest;
import com.example.Jobportal.model.RecruiterProfileResponse;

public interface RecruiterProfileService {
    RecruiterProfileResponse createProfile(Long userId, RecruiterProfileRequest request);
    RecruiterProfileResponse getProfile(Long userId);
    RecruiterProfileResponse updateProfile(Long userId, RecruiterProfileRequest request);
}