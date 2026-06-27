package com.example.Jobportal.service;

import com.example.Jobportal.dto.CandidateProfileRequest;
import com.example.Jobportal.model.CandidateProfileResponse;

import java.util.List;

public interface CandidateProfileService {
    CandidateProfileResponse createProfile(Long userId, CandidateProfileRequest request);
    CandidateProfileResponse getProfile(Long userId);
    CandidateProfileResponse updateProfile(Long userId, CandidateProfileRequest request);
    List<CandidateProfileResponse> getAllProfiles();
}