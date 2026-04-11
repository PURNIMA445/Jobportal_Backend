package com.example.Jobportal.service;

import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.model.CandidateProfile;

import java.util.List;

public interface CandidateProfileService {

    CandidateProfile createProfile(Long userId, CandidateProfile profile); // userId links the OneToOne
    CandidateProfile getProfileById(Long id);
    CandidateProfile getProfileByUserId(Long userId);                       // login: fetch own profile
    List<CandidateProfile> getAllProfiles();
    CandidateProfile updateProfile(Long id, CandidateProfile profile);
    boolean deleteProfile(Long id);

    // ── Skill management (M2M relation) ──────────────────────────────────────
    CandidateProfile addSkill(Long candidateId, Long skillId);
    CandidateProfile removeSkill(Long candidateId, Long skillId);

    // ── AI hook ──────────────────────────────────────────────────────────────
    // Called by your Python FastAPI service (via a REST endpoint in the controller)
    // to fetch all candidates who have a resumeText — these are the ones
    // the AI can encode into vectors for semantic matching.
    List<CandidateProfileEntity> getCandidatesForAIScoring();
}