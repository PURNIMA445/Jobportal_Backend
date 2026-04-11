package com.example.Jobportal.repository;

import com.example.Jobportal.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    Optional<CompanyEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    // Search companies by partial name — used in recruiter onboarding to pick their company
    List<CompanyEntity> findByNameContainingIgnoreCase(String keyword);
}