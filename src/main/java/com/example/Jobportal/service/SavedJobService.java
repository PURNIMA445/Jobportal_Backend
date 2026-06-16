package com.example.Jobportal.service;

import com.example.Jobportal.model.JobResponse;
import java.util.List;

public interface SavedJobService {
    void saveJob(Long userId, Long jobId);
    void unsaveJob(Long userId, Long jobId);
    List<JobResponse> getSavedJobs(Long userId);
}