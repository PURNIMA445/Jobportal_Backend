package com.example.Jobportal.model;

import java.time.LocalDateTime;
import java.util.Set;

// ── WHAT CHANGED ─────────────────────────────────────────────────────────────
// Old: salary (Long single field), companyName (String), postedDate (LocalDate)
//      → ALL of these caused crashes because the new entity has different fields.
//
// New:
//   salaryMin / salaryMax  → matches JobEntity's two salary fields
//   companyId / companyName → company is now a @ManyToOne relation;
//       we expose both id and name in the model for convenience
//   recruiterProfileId     → tracks which recruiter posted this job
//   postedAt (LocalDateTime) → matches entity field name exactly
//   requiredSkillNames     → flat set of skill names (not entities)
//   description            → PRIMARY AI field (Python will encode this to a vector)
//   experienceRequired     → used as AI pre-filter before embedding computation
// ─────────────────────────────────────────────────────────────────────────────
public class Job {

    private Long id;
    private String title;
    private String description;         // PRIMARY AI matching field
    private String location;
    private String jobType;
    private Double salaryMin;
    private Double salaryMax;
    private Integer experienceRequired;  // AI pre-filter field
    private Boolean isActive;
    private LocalDateTime postedAt;
    private Long companyId;
    private String companyName;          // denormalised for convenience
    private Long recruiterProfileId;
    private Set<String> requiredSkillNames;

    public Job() {}

    public Job(Long id, String title, String description, String location,
               String jobType, Double salaryMin, Double salaryMax,
               Integer experienceRequired, Boolean isActive, LocalDateTime postedAt,
               Long companyId, String companyName, Long recruiterProfileId,
               Set<String> requiredSkillNames) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.jobType = jobType;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.experienceRequired = experienceRequired;
        this.isActive = isActive;
        this.postedAt = postedAt;
        this.companyId = companyId;
        this.companyName = companyName;
        this.recruiterProfileId = recruiterProfileId;
        this.requiredSkillNames = requiredSkillNames;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public Double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Double salaryMin) { this.salaryMin = salaryMin; }

    public Double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Double salaryMax) { this.salaryMax = salaryMax; }

    public Integer getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(Integer experienceRequired) { this.experienceRequired = experienceRequired; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Long getRecruiterProfileId() { return recruiterProfileId; }
    public void setRecruiterProfileId(Long recruiterProfileId) { this.recruiterProfileId = recruiterProfileId; }

    public Set<String> getRequiredSkillNames() { return requiredSkillNames; }
    public void setRequiredSkillNames(Set<String> requiredSkillNames) { this.requiredSkillNames = requiredSkillNames; }
}