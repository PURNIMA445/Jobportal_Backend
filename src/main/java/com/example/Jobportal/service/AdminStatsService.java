package com.example.Jobportal.service;

import java.util.Map;

public interface AdminStatsService {
    Map<String, Object> getStats(Long adminId);
}
