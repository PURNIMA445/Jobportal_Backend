package com.example.Jobportal.service;

import com.example.Jobportal.dto.JobRequest;
import com.example.Jobportal.model.JobResponse;
import java.util.List;

public interface JobService {
    JobResponse createJob(Long userId, JobRequest request);
    JobResponse getJob(Long id);
    List<JobResponse> getAllOpenJobs();
    List<JobResponse> getJobsByRecruiter(Long userId);
    List<JobResponse> searchJobs(String keyword);
    JobResponse closeJob(Long jobId, Long userId);
}