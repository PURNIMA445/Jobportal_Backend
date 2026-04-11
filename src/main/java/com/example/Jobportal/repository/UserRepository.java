package com.example.Jobportal.repository;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Used during login / JWT auth to fetch user by email
    Optional<UserEntity> findByEmail(String email);

    // Prevent duplicate registrations
    boolean existsByEmail(String email);

    // Admin dashboard: list all users of a given role
    List<UserEntity> findByRole(Role role);
}