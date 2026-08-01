package com.example.Jobportal.service;

public interface EmailVerificationService {
    void sendVerificationOtp(String email);
    void verifyEmail(String email, String otp);
}
