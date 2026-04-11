package com.example.Jobportal.service;

import com.example.Jobportal.model.RecruiterProfile;

import java.util.List;

public interface RecruiterProfileService {

    RecruiterProfile createProfile(Long userId, Long companyId, RecruiterProfile profile);
    RecruiterProfile getProfileById(Long id);
    RecruiterProfile getProfileByUserId(Long userId);          // fetch own profile after login
    List<RecruiterProfile> getAllProfiles();
    List<RecruiterProfile> getProfilesByCompany(Long companyId);
    RecruiterProfile updateProfile(Long id, RecruiterProfile profile);
    RecruiterProfile assignToCompany(Long profileId, Long companyId); // change company
    boolean deleteProfile(Long id);
}