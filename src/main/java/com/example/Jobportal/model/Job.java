package com.example.Jobportal.model;
import com.example.Jobportal.enums.JobType;


import java.time.LocalDate;

public class Job {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Long salary;
    private int experience;
    private JobType jobType;
    private String companyName;
    private LocalDate postedDate;

    public Job(Long id, String title, String description, String location, Long salary, int experience, JobType jobType, String companyName, LocalDate postedDate, String location1) {
    }

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

    public Job(Long id, String title, String description, String location, Long salary, int experience, JobType jobType, String companyName, LocalDate postedDate) {
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
    public Job() {
    }



}
