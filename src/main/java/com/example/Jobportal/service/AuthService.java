package com.example.Jobportal.service;

import com.example.Jobportal.dto.LoginRequest;
import com.example.Jobportal.dto.SignupRequest;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.model.AuthResponse;
import com.example.Jobportal.model.User;
import com.example.Jobportal.dto.ResetPasswordRequest;
import com.example.Jobportal.dto.ChangePasswordRequest;
public interface AuthService {
    User signup(SignupRequest request);
    AuthResponse login(LoginRequest request);

    AuthResponse loginWithFirebase(String idToken, Role requestedRole, boolean allowCreate);
    void sendVerificationOtp(String email);
    void verifyEmail(String email, String otp);
    void sendPasswordResetOtp(String email);
    String resetPassword(ResetPasswordRequest request);
    String changePassword(Long userId, ChangePasswordRequest request);
    String deleteAccount(Long userId);
}