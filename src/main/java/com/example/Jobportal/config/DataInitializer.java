package com.example.Jobportal.config;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "support@seepsetu.com";
        UserEntity admin = userRepository.findByEmail(adminEmail).orElse(null);
        if (admin == null) {
            admin = new UserEntity();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("seepsetu123"));
            admin.setRole(Role.ADMIN);
            admin.setIsEmailVerified(true);
            userRepository.save(admin);
        } else if (admin.getRole() != Role.ADMIN || !admin.getIsEmailVerified()) {
            admin.setRole(Role.ADMIN);
            admin.setIsEmailVerified(true);
            admin.setPassword(passwordEncoder.encode("seepsetu123"));
            userRepository.save(admin);
        }
    }
}
