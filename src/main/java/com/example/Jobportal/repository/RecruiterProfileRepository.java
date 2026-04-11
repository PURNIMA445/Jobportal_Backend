package com.example.Jobportal.repository;

import com.example.Jobportal.entity.RecruiterProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfileEntity, Long> {

    Optional<RecruiterProfileEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    // Find all recruiters belonging to a company (e.g., for company admin views)
    List<RecruiterProfileEntity> findByCompanyId(Long companyId);
}