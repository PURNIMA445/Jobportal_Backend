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

    public void sendInviteEmail(String toEmail, String companyName, String inviteLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Invitation to join " + companyName + " on Smart Job Portal");
        message.setText(
                "You have been invited to join the team for " + companyName + ".\n\n" +
                        "Click the link below to accept the invitation and join the company:\n" +
                        inviteLink + "\n\n" +
                        "This link will expire in 48 hours."
        );
        mailSender.send(message);
    }

    public void sendCompanyVerificationEmail(String toEmail, String companyName, boolean approved, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        if (approved) {
            message.setSubject("Company Approved: " + companyName);
            message.setText("Great news! Your company profile for " + companyName + " has been approved.\nYou can now post jobs and invite team members.");
        } else {
            message.setSubject("Company Registration Rejected: " + companyName);
            message.setText("Unfortunately, your company registration for " + companyName + " was rejected.\nReason: " + reason);
        }
        mailSender.send(message);
    }
}