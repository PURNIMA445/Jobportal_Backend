package com.example.Jobportal.controller;

import com.example.Jobportal.model.CandidateProfile;
import com.example.Jobportal.model.Job;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Jobportal.service.CandidateProfileService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class CandidateProfileController {
    private final CandidateProfileService candidateProfileService;
    public CandidateProfileController(CandidateProfileService candidateProfileService)
    {
        this.candidateProfileService=candidateProfileService;
    }
    @PostMapping("/profile")
    public CandidateProfile saveCandidateProfile(@RequestBody CandidateProfile candidateProfile)
    {
        return candidateProfileService.saveCandidateProfile(candidateProfile);
    }
    @GetMapping("/profile")
    public List<CandidateProfile> getAllProfiles()
    {

        return candidateProfileService.getAllProfiles();
    }
    @GetMapping("profile/{id}")
    public ResponseEntity<CandidateProfile> getProfileById(@PathVariable Long id)
    {
        CandidateProfile candidateProfile;
        candidateProfile = candidateProfileService.getProfileById(id);
        return ResponseEntity.ok(candidateProfile);
    }
    @DeleteMapping("/profile/{id}")
    public  ResponseEntity<Map<String,Boolean>> deleteProfile(@PathVariable Long id)
    {
        boolean deleted;
        deleted=candidateProfileService.deleteProfile(id);
        Map<String,Boolean> response= new HashMap<>();
        response.put("deleted",deleted);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/profile/{id}")
    public ResponseEntity<CandidateProfile>updateProfile(@PathVariable Long id,
                                        @RequestBody CandidateProfile candidateProfile){
        candidateProfile=candidateProfileService.updateProfile(id,candidateProfile);
        return ResponseEntity.ok(candidateProfile);

    }


}
