package com.example.Jobportal.service;

import com.example.Jobportal.model.RecruiterProfile;

import java.util.List;

public interface RecruiterProfileService {

    RecruiterProfile createProfile(RecruiterProfile recruiterProfile);

    List<RecruiterProfile> getAllProfiles();

    RecruiterProfile getProfileById(Long id);

    RecruiterProfile updateProfile(Long id, RecruiterProfile recruiterProfile);

    boolean deleteProfile(Long id);
}