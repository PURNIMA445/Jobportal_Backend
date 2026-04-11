package com.example.Jobportal.repository;

import com.example.Jobportal.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Long> {

    Optional<SkillEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    // Bulk-fetch skills by name — used when a candidate/job submits a list of skill names
    // and we need to resolve them to entities without N+1 queries
    List<SkillEntity> findByNameIgnoreCaseIn(Set<String> names);

    // Find skills linked to a specific job — used by AI pre-filter to check
    // candidate skill overlap before running expensive embedding computation
    @Query("SELECT s FROM SkillEntity s JOIN s.jobs j WHERE j.id = :jobId")
    List<SkillEntity> findSkillsByJobId(@Param("jobId") Long jobId);

    // Find skills linked to a candidate profile
    @Query("SELECT s FROM SkillEntity s JOIN s.candidates c WHERE c.id = :candidateId")
    List<SkillEntity> findSkillsByCandidateId(@Param("candidateId") Long candidateId);
}