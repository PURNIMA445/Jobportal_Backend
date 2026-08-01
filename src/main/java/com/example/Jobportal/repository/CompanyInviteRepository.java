package com.example.Jobportal.repository;

import com.example.Jobportal.entity.CompanyInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CompanyInviteRepository extends JpaRepository<CompanyInviteEntity, Long> {
    Optional<CompanyInviteEntity> findByToken(String token);
    Optional<CompanyInviteEntity> findByCompanyIdAndEmail(Long companyId, String email);
    List<CompanyInviteEntity> findByCompanyId(Long companyId);
}
