package com.example.Jobportal.service;

import com.example.Jobportal.model.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getStats(Long userId);
}
