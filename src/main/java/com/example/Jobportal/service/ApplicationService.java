package com.example.Jobportal.service;

import com.example.Jobportal.enums.AppStatus;
import com.example.Jobportal.model.ApplicationResponse;
import java.util.List;

public interface ApplicationService {
    ApplicationResponse apply(Long userId, Long jobId, String coverLetter);
    List<ApplicationResponse> getMyApplications(Long userId);
    List<ApplicationResponse> getJobApplications(Long jobId, Long userId);
    ApplicationResponse updateStatus(Long applicationId, Long userId, AppStatus status);
}