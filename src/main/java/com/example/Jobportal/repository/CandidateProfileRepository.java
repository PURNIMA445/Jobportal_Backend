package com.example.Jobportal.repository;

import com.example.Jobportal.entity.CandidateProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfileEntity, Long> {

    // Fetch a candidate profile directly from a userId (avoids extra join through UserEntity)
    Optional<CandidateProfileEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    // ── AI PRE-FILTER QUERY ───────────────────────────────────────────────────
    // Before running the Transformer model, filter candidates by:
    //   1. Minimum years of experience (numeric filter — cheap SQL check)
    //   2. At least one matching skill from the job's required skills (set intersection)
    // This narrows the candidate pool so the AI only scores genuinely eligible candidates,
    // dramatically reducing embedding computation cost.
    @Query("""
        SELECT DISTINCT c FROM CandidateProfileEntity c
        JOIN c.skills s
        WHERE c.yearsOfExperience >= :minExperience
        AND s.id IN :skillIds
    """)
    List<CandidateProfileEntity> findEligibleCandidates(
            @Param("minExperience") Integer minExperience,
            @Param("skillIds") List<Long> skillIds
    );

    // Fetch candidates whose resumeText is not null — required before the AI can encode them.
    // Used by a batch job that pre-computes and caches candidate embeddings.
    @Query("SELECT c FROM CandidateProfileEntity c WHERE c.resumeText IS NOT NULL AND c.resumeText <> ''")
    List<CandidateProfileEntity> findCandidatesWithResume();

    // Eager-fetch skills alongside profile in one query to avoid N+1 when
    // the service needs to build the SkillMatchDTO for the AI pre-filter.
    @Query("SELECT c FROM CandidateProfileEntity c LEFT JOIN FETCH c.skills WHERE c.id = :id")
    Optional<CandidateProfileEntity> findByIdWithSkills(@Param("id") Long id);
}