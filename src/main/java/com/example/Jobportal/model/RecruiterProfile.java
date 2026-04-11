package com.example.Jobportal.model;

// ── WHAT CHANGED ─────────────────────────────────────────────────────────────
// Old: userId (Long), companyName (String) — flat fields that matched the OLD entity
//      which stored userId and companyName as plain columns.
//
// New: userId still here (we resolve it from entity.getUser().getId())
//      companyId + companyName — company is now @ManyToOne CompanyEntity,
//      so we extract both id and name from the relation for the response.
//      Added designation — this field exists on the new entity.
// ─────────────────────────────────────────────────────────────────────────────
public class RecruiterProfile {

    private Long id;
    private Long userId;
    private String fullName;     // from User relation
    private Long companyId;
    private String companyName;  // from Company relation
    private String designation;
    private String jobTitle;
    private String specialization;
    private String companyWebsite;
    private String linkedInUrl;
    private String phoneNumber;

    public RecruiterProfile() {}

    public RecruiterProfile(Long id, Long userId, String fullName, Long companyId,
                            String companyName, String designation, String jobTitle,
                            String specialization, String companyWebsite,
                            String linkedInUrl, String phoneNumber) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.companyId = companyId;
        this.companyName = companyName;
        this.designation = designation;
        this.jobTitle = jobTitle;
        this.specialization = specialization;
        this.companyWebsite = companyWebsite;
        this.linkedInUrl = linkedInUrl;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getCompanyWebsite() { return companyWebsite; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }

    public String getLinkedInUrl() { return linkedInUrl; }
    public void setLinkedInUrl(String linkedInUrl) { this.linkedInUrl = linkedInUrl; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}