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

    String deactivateAccount(Long userId);
}