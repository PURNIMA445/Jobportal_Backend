package com.example.Jobportal.service;

import com.example.Jobportal.dto.ResetPasswordRequest;
import com.example.Jobportal.dto.ChangePasswordRequest;

public interface PasswordResetService {
    void sendPasswordResetOtp(String email);
    String resetPassword(ResetPasswordRequest request);
    String changePassword(Long userId, ChangePasswordRequest request);
}
