package com.example.Jobportal.config;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        String adminEmail = "support@seepsetu.com";

        try {
            java.util.Optional<UserEntity> existingUser = userRepository.findByEmail(adminEmail);
            if (existingUser.isPresent()) {
                UserEntity admin = existingUser.get();
                admin.setRole(Role.ADMIN);
                admin.setIsEmailVerified(true);
                admin.setPassword(passwordEncoder.encode("seepsetu123"));
                userRepository.save(admin);
                log.info("✅ Admin user already exists. Ensured ADMIN role and correct password.");
                return;
            }

            // Use setters instead of builder to ensure @PrePersist runs properly
            UserEntity admin = new UserEntity();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("seepsetu123"));
            admin.setRole(Role.ADMIN);
            admin.setIsEmailVerified(true);

            userRepository.save(admin);
            log.info("✅ Admin user created: {}", adminEmail);
        } catch (Exception e) {
            log.error("❌ Failed to create/update admin user: {}", e.getMessage());
        }
    }
}
