package com.example.Jobportal.model;

import java.util.Set;

// ── WHAT CHANGED ─────────────────────────────────────────────────────────────
// Old: only id, currentJobTitle, currentCompany  (3 fields, useless for AI)
// New: added resumeText, summary, yearsOfExperience, skillNames, userId
//
// resumeText + summary → these are the fields the Python AI service will read
//   to generate sentence embeddings for semantic job matching.
// skillNames (Set<String>) → flat set of skill names for the response.
//   We don't expose SkillEntity directly to avoid leaking DB structure.
// userId → lets the frontend know which user owns this profile.
// ─────────────────────────────────────────────────────────────────────────────
public class CandidateProfile {

    private Long id;
    private Long userId;            // FK back to UserEntity
    private String resumeText;      // PRIMARY AI input field
    private String summary;         // SECONDARY AI input field
    private Integer yearsOfExperience;
    private String location;
    private String phoneNumber;
    private String linkedinUrl;
    private String portfolioUrl;
    private Set<String> skillNames; // flat names, not entity objects

    public CandidateProfile() {}

    public CandidateProfile(Long id, Long userId, String resumeText, String summary,
                            Integer yearsOfExperience, String location,
                            String phoneNumber, String linkedinUrl,
                            String portfolioUrl, Set<String> skillNames) {
        this.id = id;
        this.userId = userId;
        this.resumeText = resumeText;
        this.summary = summary;
        this.yearsOfExperience = yearsOfExperience;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.linkedinUrl = linkedinUrl;
        this.portfolioUrl = portfolioUrl;
        this.skillNames = skillNames;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public Set<String> getSkillNames() { return skillNames; }
    public void setSkillNames(Set<String> skillNames) { this.skillNames = skillNames; }
}