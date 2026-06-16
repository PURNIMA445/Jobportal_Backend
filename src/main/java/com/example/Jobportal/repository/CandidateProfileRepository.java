package com.example.Jobportal.repository;

import com.example.Jobportal.entity.CandidateProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfileEntity, Long> {
    Optional<CandidateProfileEntity> findByUserId(Long userId);
    Boolean existsByUserId(Long userId);
}