package com.example.Jobportal.service;

import com.example.Jobportal.model.CandidateProfile;

import java.util.List;

public interface CandidateProfileService {
    CandidateProfile saveCandidateProfile(CandidateProfile candidateProfile);

    List<CandidateProfile> getAllProfiles();

    CandidateProfile getProfileById(Long id);

    boolean deleteProfile(Long id);

    CandidateProfile updateProfile(Long id, CandidateProfile candidateProfile);
}
