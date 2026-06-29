package com.example.Jobportal.repository;

import com.example.Jobportal.entity.RecruiterProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfileEntity, Long> {
    Optional<RecruiterProfileEntity> findByUserId(Long userId);
    Boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
}