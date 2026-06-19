package com.example.Jobportal.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUtils {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp, String purpose) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Smart Job Portal — Your verification code");
        message.setText(
                "Your OTP code is: " + otp + "\n\n" +
                        "This code is for: " + purpose + "\n" +
                        "It will expire in 10 minutes.\n\n" +
                        "If you didn't request this, please ignore this email."
        );
        mailSender.send(message);
    }
}