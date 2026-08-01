package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.ChangePasswordRequest;
import com.example.Jobportal.dto.ResetPasswordRequest;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.PasswordResetService;
import com.example.Jobportal.utils.EmailUtils;
import com.example.Jobportal.utils.OtpUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtils emailUtils;
    private final OtpUtils otpUtils;

    @Override
    @Transactional
    public void sendPasswordResetOtp(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));
        String otp = otpUtils.generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        emailUtils.sendOtpEmail(email, otp, "Password Reset");
    }

    @Override
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOtpCode() == null || !user.getOtpCode().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired, please request a new one");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        return "Password reset successfully";
    }

    @Override
    @Transactional
    public String changePassword(Long userId, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }
}
