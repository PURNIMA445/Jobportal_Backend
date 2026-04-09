package com.example.Jobportal.controller;

import com.example.Jobportal.model.RecruiterProfile;
import com.example.Jobportal.service.RecruiterProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    public RecruiterProfileController(RecruiterProfileService recruiterProfileService) {
        this.recruiterProfileService = recruiterProfileService;
    }

    // Create recruiter profile
    @PostMapping("/recruiter-profile")
    public RecruiterProfile createProfile(@RequestBody RecruiterProfile profile) {
        return recruiterProfileService.createProfile(profile);
    }

    // Get all recruiter profiles
    @GetMapping("/recruiter-profile")
    public List<RecruiterProfile> getAllProfiles() {
        return recruiterProfileService.getAllProfiles();
    }

    // Get profile by ID
    @GetMapping("/recruiter-profile/{id}")
    public ResponseEntity<RecruiterProfile> getProfileById(@PathVariable Long id) {
        RecruiterProfile profile = recruiterProfileService.getProfileById(id);
        return ResponseEntity.ok(profile);
    }

    // Update profile by ID
    @PutMapping("/recruiter-profile/{id}")
    public ResponseEntity<RecruiterProfile> updateProfile(@PathVariable Long id,
                                                          @RequestBody RecruiterProfile profile) {
        RecruiterProfile updatedProfile = recruiterProfileService.updateProfile(id, profile);
        return ResponseEntity.ok(updatedProfile);
    }

    // Delete profile by ID
    @DeleteMapping("/recruiter-profile/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteProfile(@PathVariable Long id) {
        boolean deleted = recruiterProfileService.deleteProfile(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", deleted);
        return ResponseEntity.ok(response);
    }
}