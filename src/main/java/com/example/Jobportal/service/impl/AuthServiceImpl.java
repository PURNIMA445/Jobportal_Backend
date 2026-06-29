package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.SignupRequest;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.exception.EmailNotVerifiedException;
import com.example.Jobportal.model.User;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
        sendVerificationOtp(saved.getEmail());
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
    public String verifyEmail(String email, String otp) {
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
        return "Email verified successfully";
    }

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

    @Override
    @Transactional
    public String deleteAccount(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.RECRUITER) {
            var recruiter = recruiterProfileRepository.findByUserId(userId).orElse(null);
            if (recruiter != null) {
                var jobs = jobRepository.findByRecruiterId(recruiter.getId());
                if (!jobs.isEmpty()) {
                    var jobIds = jobs.stream().map(j -> j.getId()).toList();
                    var apps = applicationRepository.findByJobIdIn(jobIds);
                    boolean hasActive = apps.stream()
                            .anyMatch(a -> a.getStatus() != com.example.Jobportal.enums.AppStatus.REJECTED);
                    if (hasActive) {
                        throw new RuntimeException(
                                "Cannot delete account: you have job posts with active applicants."
                        );
                    }
                    applicationRepository.deleteByJobIdIn(jobIds);
                    jobRepository.deleteAll(jobs);
                }
                recruiterProfileRepository.deleteByUserId(userId);
            }
        } else if (user.getRole() == Role.CANDIDATE) {
            var candidate = candidateProfileRepository.findByUserId(userId).orElse(null);
            if (candidate != null) {
                applicationRepository.deleteByCandidateId(candidate.getId());
                savedJobRepository.deleteByCandidateId(candidate.getId());
                candidateProfileRepository.deleteByUserId(userId);
            }
        }

        notificationRepository.deleteByUserId(userId);
        userRepository.delete(user);
        return "Account deleted successfully";
    }
    @Override
    public AuthResponse login(LoginRequest request) {

        // 1. Find user by email
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!user.getIsEmailVerified()) {
            sendVerificationOtp(user.getEmail());
            throw new EmailNotVerifiedException(user.getEmail());
        }
        // 2. Compare passwords
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
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


    // --- NEW METHOD FOR GOOGLE LOGIN ---
    @Override
    public AuthResponse processSocialLogin(String email, Role role) {
        
        // 1. Check if a user with this Google email already exists in our database
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        UserEntity user;

        if (userOptional.isPresent()) {
            // User exists! Just log them in.
            user = userOptional.get();
        } else {
            // User does NOT exist! Create a new account for them automatically.
            user = UserEntity.builder()
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random secure password
                    .role(role != null ? role : Role.CANDIDATE) // Default to CANDIDATE if missing
                    .isEmailVerified(true) // Set to true because Google already verified their email!
                    .build();
            
            // Save the new user to the database
            user = userRepository.save(user);
        }

        // 2. Generate JWT using your specific JwtUtils signature
        String token = jwtUtils.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        // 3. Return the exact AuthResponse structure your app expects
        return new AuthResponse(token, user.getRole().name(), user.getId());
    }


    @Transactional
    @Override
    public AuthResponse loginWithFirebase(String idToken, Role requestedRole) {
        FirebaseToken decodedToken;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired Firebase token");
        }

        String email = decodedToken.getEmail();
        if (email == null) {
            throw new RuntimeException("No email found on this Firebase account");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .email(email)
                            .googleId(decodedToken.getUid())
                            .role(requestedRole)          // ← uses the choice, only for NEW users
                            .isEmailVerified(true)
                            .build();
                    return userRepository.save(newUser);
                });

        String token = jwtUtils.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        return new AuthResponse(token, user.getRole().name(), user.getId());
    }
}