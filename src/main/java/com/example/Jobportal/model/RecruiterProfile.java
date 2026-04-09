package com.example.Jobportal.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruiterProfile {

    private Long id;
    private Long userId;
    private String companyName;
    private String jobTitle;
    private String specialization;
    private String companyWebsite;
    private String linkedInUrl;

    // No-args constructor
    public RecruiterProfile() {}

    // Parameterized constructor
    public RecruiterProfile(Long id, Long userId, String companyName,
                            String jobTitle, String specialization,
                            String companyWebsite, String linkedInUrl) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.specialization = specialization;
        this.companyWebsite = companyWebsite;
        this.linkedInUrl = linkedInUrl;
    }
}