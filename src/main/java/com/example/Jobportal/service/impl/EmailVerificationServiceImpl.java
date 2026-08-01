package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.EmailVerificationService;
import com.example.Jobportal.utils.EmailUtils;
import com.example.Jobportal.utils.OtpUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailUtils emailUtils;
    private final OtpUtils otpUtils;

    @Override
    @Transactional
    public void sendVerificationOtp(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getIsEmailVerified()) {
            throw new RuntimeException("Email already verified");
        }
        String otp = otpUtils.generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        emailUtils.sendOtpEmail(email, otp, "Email Verification");
    }

    @Override
    @Transactional
    public void verifyEmail(String email, String otp) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOtpCode() == null || !user.getOtpCode().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired, please request a new one");
        }
        user.setIsEmailVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }
}
