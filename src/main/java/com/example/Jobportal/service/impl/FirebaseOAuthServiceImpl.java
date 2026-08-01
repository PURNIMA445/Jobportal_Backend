package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.model.AuthResponse;
import com.example.Jobportal.repository.CandidateProfileRepository;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.FirebaseOAuthService;
import com.example.Jobportal.utils.JwtUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseOAuthServiceImpl implements FirebaseOAuthService {

    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public AuthResponse loginWithFirebase(String idToken, Role requestedRole, boolean allowCreate) {
        if (requestedRole == Role.ADMIN) {
            throw new RuntimeException("Unauthorized role assignment");
        }
        
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

        // Extract profile data available in the Firebase token
        String displayName = decodedToken.getName();           // Full name from Google/GitHub
        String photoUrl    = decodedToken.getPicture();        // Profile photo URL

        boolean isNewUser = !userRepository.existsByEmail(email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    if (!allowCreate) {
                        throw new RuntimeException("No account found with this email. Please sign up first.");
                    }
                    UserEntity newUser = UserEntity.builder()
                            .email(email)
                            .googleId(decodedToken.getUid())
                            .role(requestedRole)
                            .isEmailVerified(true)
                            .build();
                    return userRepository.save(newUser);
                });

        // If user is deactivated, reactivate them on successful login
        if (user.getIsActive() == null || !user.getIsActive()) {
            user.setIsActive(true);
            userRepository.save(user);
        }

        // Auto-create profile for brand new social login users
        boolean profileCreated = false;
        if (isNewUser && allowCreate) {
            String name = (displayName != null && !displayName.isBlank()) ? displayName : email.split("@")[0];
            profileCreated = autoCreateProfile(user, name, photoUrl);
        }

        String token = jwtUtils.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        return new AuthResponse(token, user.getRole().name(), user.getId(), profileCreated);
    }

    /**
     * Silently creates a candidate or recruiter profile from OAuth data.
     * Returns true if profile was successfully created.
     */
    private boolean autoCreateProfile(UserEntity user, String fullName, String photoUrl) {
        try {
            if (user.getRole() == Role.CANDIDATE) {
                if (!candidateProfileRepository.existsByUserId(user.getId())) {
                    CandidateProfileEntity profile = CandidateProfileEntity.builder()
                            .user(user)
                            .fullName(fullName)
                            .profilePicUrl(photoUrl)
                            .build();
                    candidateProfileRepository.save(profile);
                    return true;
                }
            } else if (user.getRole() == Role.RECRUITER) {
                if (!recruiterProfileRepository.existsByUserId(user.getId())) {
                    RecruiterProfileEntity profile = RecruiterProfileEntity.builder()
                            .user(user)
                            .fullName(fullName)
                            .build();
                    recruiterProfileRepository.save(profile);
                    return true;
                }
            }
        } catch (Exception e) {
            // Non-fatal: if profile creation fails, user can still login and fill form
            System.err.println("Auto-profile creation failed for user " + user.getEmail() + ": " + e.getMessage());
        }
        return false;
    }
}
