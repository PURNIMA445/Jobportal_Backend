package com.example.Jobportal.service;

import com.example.Jobportal.dto.CandidateProfileRequest;
import com.example.Jobportal.model.CandidateProfileResponse;

public interface CandidateProfileService {
    CandidateProfileResponse createProfile(Long userId, CandidateProfileRequest request);
    CandidateProfileResponse getProfile(Long userId);
    CandidateProfileResponse updateProfile(Long userId, CandidateProfileRequest request);
}