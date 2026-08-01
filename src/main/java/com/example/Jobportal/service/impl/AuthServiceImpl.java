package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.SignupRequest;
import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.exception.EmailNotVerifiedException;
import com.example.Jobportal.model.User;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Jobportal.service.EmailVerificationService;
import com.example.Jobportal.dto.LoginRequest;
import com.example.Jobportal.model.AuthResponse;
import com.example.Jobportal.utils.JwtUtils;
import com.example.Jobportal.enums.Role;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import java.util.Optional;
import java.util.UUID;
import com.example.Jobportal.dto.ResetPasswordRequest;
import com.example.Jobportal.dto.ChangePasswordRequest;
import com.example.Jobportal.utils.EmailUtils;
import com.example.Jobportal.utils.OtpUtils;
import com.example.Jobportal.repository.CandidateProfileRepository;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import com.example.Jobportal.repository.JobRepository;
import com.example.Jobportal.repository.ApplicationRepository;
import com.example.Jobportal.repository.SavedJobRepository;
import com.example.Jobportal.repository.NotificationRepository;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailUtils emailUtils;
    private final OtpUtils otpUtils;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final NotificationRepository notificationRepository;
    private final EmailVerificationService emailVerificationService;

    @Override
    public User signup(SignupRequest request) {

        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // 2. Build the entity and hash the password
        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isEmailVerified(false)
                .build();

        // 3. Save to database
        UserEntity saved = userRepository.save(user);
        emailVerificationService.sendVerificationOtp(saved.getEmail());
        // 4. Return response model (never the entity)
        return new User(
                saved.getId(),
                saved.getEmail(),
                saved.getRole(),
                saved.getIsEmailVerified()
        );
    }


    @Override
    @Transactional
    public String deactivateAccount(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.RECRUITER) {
            var recruiter = recruiterProfileRepository.findByUserId(userId).orElse(null);
            if (recruiter != null) {
                var jobs = jobRepository.findByRecruiterId(recruiter.getId());
                if (!jobs.isEmpty()) {
                    jobs.forEach(job -> {
                        if (job.getStatus() == com.example.Jobportal.enums.JobStatus.OPEN) {
                            job.setStatus(com.example.Jobportal.enums.JobStatus.CLOSED);
                        }
                    });
                    jobRepository.saveAll(jobs);
                }
            }
        }

        user.setIsActive(false);
        userRepository.save(user);
        return "Account deactivated successfully";
    }
    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!user.getIsEmailVerified()) {
            emailVerificationService.sendVerificationOtp(user.getEmail());
            throw new EmailNotVerifiedException(user.getEmail());
        }
        // 2. Compare passwords
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // If user is deactivated, reactivate them on successful login
        if (user.getIsActive() == null || !user.getIsActive()) {
            user.setIsActive(true);
            userRepository.save(user);
        }

        // 3. Generate JWT
        String token = jwtUtils.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );
        // 4. Return token + role + userId
        return new AuthResponse(token, user.getRole().name(), user.getId());
    }


}