package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.SignupRequest;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.model.User;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Jobportal.dto.LoginRequest;
import com.example.Jobportal.model.AuthResponse;
import com.example.Jobportal.utils.JwtUtils;
import com.example.Jobportal.enums.Role;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

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

        // 4. Return response model (never the entity)
        return new User(
                saved.getId(),
                saved.getEmail(),
                saved.getRole(),
                saved.getIsEmailVerified()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // 1. Find user by email
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

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
}