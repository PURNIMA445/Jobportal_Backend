package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    private void verifyAdmin(Long userId) {
        if (userId == null) {
            throw new RuntimeException("Access denied");
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }
    }

    @Override
    public List<Map<String, Object>> getAllUsers(Long adminId) {
        verifyAdmin(adminId);
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("email", u.getEmail());
            map.put("role", u.getRole() != null ? u.getRole().name() : "UNKNOWN");
            map.put("isEmailVerified", u.getIsEmailVerified() != null ? u.getIsEmailVerified() : false);
            map.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserRole(Long adminId, Long targetUserId, String newRoleStr) {
        verifyAdmin(adminId);
        if (adminId.equals(targetUserId)) {
            throw new RuntimeException("Cannot change your own role");
        }
        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            Role newRole = Role.valueOf(newRoleStr.toUpperCase());
            targetUser.setRole(newRole);
            userRepository.save(targetUser);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role format");
        }
    }

    @Override
    @Transactional
    public void toggleUserVerification(Long adminId, Long targetUserId, Boolean isEmailVerified) {
        verifyAdmin(adminId);
        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (isEmailVerified == null) {
            throw new RuntimeException("isEmailVerified field is required");
        }
        targetUser.setIsEmailVerified(isEmailVerified);
        userRepository.save(targetUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long adminId, Long targetUserId) {
        verifyAdmin(adminId);
        if (adminId.equals(targetUserId)) {
            throw new RuntimeException("Cannot delete yourself");
        }
        if (!userRepository.existsById(targetUserId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(targetUserId);
    }
}
