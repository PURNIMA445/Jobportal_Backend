package com.example.Jobportal.service;

import com.example.Jobportal.model.Job;

import java.util.List;

public interface JobService {
    Job getJobById(Long id);
    boolean deleteJob(Long id);
    Job updateJob(Long id, Job job);
    Job saveJob(Job job);
    List<Job> getAllJobs();
}
