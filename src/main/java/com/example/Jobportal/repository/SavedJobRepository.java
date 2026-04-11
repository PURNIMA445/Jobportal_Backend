package com.example.Jobportal.repository;

import com.example.Jobportal.entity.SavedJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJobEntity, Long> {

    // All jobs saved by a candidate, most recently saved first
    List<SavedJobEntity> findByCandidateIdOrderBySavedAtDesc(Long candidateId);

    // Check if a candidate already saved a specific job (for toggle button state)
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    // Find the specific saved record to delete it (unsave)
    Optional<SavedJobEntity> findByCandidateIdAndJobId(Long candidateId, Long jobId);

    // ── AI SIGNAL QUERY ───────────────────────────────────────────────────────
    // Returns saved jobs where the stored matchScore was high (>= threshold).
    // The Python AI trainer can use this list as a POSITIVE TRAINING SIGNAL:
    // "candidate saved this job AND it had a high AI score" = strong implicit feedback
    // that helps fine-tune future recommendations for similar candidate profiles.
    @Query("""
        SELECT s FROM SavedJobEntity s
        WHERE s.candidate.id = :candidateId
        AND s.matchScoreAtSave >= :scoreThreshold
        ORDER BY s.matchScoreAtSave DESC
    """)
    List<SavedJobEntity> findHighScoreSavedJobs(
            @Param("candidateId") Long candidateId,
            @Param("scoreThreshold") Double scoreThreshold
    );
}