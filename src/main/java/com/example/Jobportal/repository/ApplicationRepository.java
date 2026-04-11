package com.example.Jobportal.repository;

import com.example.Jobportal.entity.ApplicationEntity;
import com.example.Jobportal.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    // Check for duplicate applications (enforced at DB level too via @UniqueConstraint)
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    // Candidate's application history
    List<ApplicationEntity> findByCandidateId(Long candidateId);

    // All applications for a specific job — ordered by matchScore DESC so the
    // recruiter sees the best AI-ranked candidates at the top immediately
    List<ApplicationEntity> findByJobIdOrderByMatchScoreDesc(Long jobId);

    // Filter applications by status (e.g., show only SHORTLISTED candidates)
    List<ApplicationEntity> findByJobIdAndStatus(Long jobId, ApplicationStatus status);

    // Find a specific candidate's application for a specific job
    Optional<ApplicationEntity> findByCandidateIdAndJobId(Long candidateId, Long jobId);

    // ── AI SCORE WRITE-BACK ───────────────────────────────────────────────────
    // This is the MOST IMPORTANT query for the Smart Recruitment goal.
    // After the AI service computes cosine_similarity(candidateEmbedding, jobEmbedding),
    // it calls this method to persist the score without loading the full entity.
    // @Modifying + @Transactional makes this a direct UPDATE statement (no fetch needed).
    @Modifying
    @Transactional
    @Query("UPDATE ApplicationEntity a SET a.matchScore = :score WHERE a.id = :applicationId")
    void updateMatchScore(@Param("applicationId") Long applicationId, @Param("score") Double score);

    // ── BATCH AI SCORE UPDATE ─────────────────────────────────────────────────
    // Fetch all applications for a job where matchScore is null
    // (i.e., newly applied candidates who haven't been scored by the AI yet).
    // A scheduled batch job uses this to process unscored applications in bulk.
    @Query("SELECT a FROM ApplicationEntity a WHERE a.job.id = :jobId AND a.matchScore IS NULL")
    List<ApplicationEntity> findUnscoredApplicationsByJob(@Param("jobId") Long jobId);

    // Top-N candidates for a job ranked by AI score — used by recruiter dashboard widget
    @Query("""
        SELECT a FROM ApplicationEntity a
        WHERE a.job.id = :jobId
        AND a.matchScore IS NOT NULL
        ORDER BY a.matchScore DESC
        LIMIT :topN
    """)
    List<ApplicationEntity> findTopScoredCandidatesForJob(
            @Param("jobId") Long jobId,
            @Param("topN") int topN
    );
}