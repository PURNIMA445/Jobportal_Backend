package com.example.Jobportal.entity;

import com.example.Jobportal.enums.JobType;
import jakarta.persistence.*;


import java.time.LocalDate;

@Entity
@Table(name = "jobs")
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 2000)
    private String description;
    private String location;
    private Long salary;
    private int experience;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name="job_type")
    private JobType jobType;

    @Column(name="company_name")
    private String companyName;
    @Column(name="posted_date")
    private LocalDate postedDate;

    public JobEntity() {
    }

    public JobEntity(Long id, String title, String description,
                     String location, Long salary, int experience,
                     JobType jobType, String companyName,
                     LocalDate postedDate) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.experience = experience;
        this.jobType = jobType;
        this.companyName = companyName;
        this.postedDate = postedDate;
    }


}
