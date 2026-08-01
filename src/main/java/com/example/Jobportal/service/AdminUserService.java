package com.example.Jobportal.service;

import java.util.List;
import java.util.Map;

public interface AdminUserService {
    List<Map<String, Object>> getAllUsers(Long adminId);
    void updateUserRole(Long adminId, Long targetUserId, String newRoleStr);
    void toggleUserVerification(Long adminId, Long targetUserId, Boolean isEmailVerified);
    void deleteUser(Long adminId, Long targetUserId);
}
