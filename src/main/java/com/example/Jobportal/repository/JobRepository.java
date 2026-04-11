package com.example.Jobportal.repository;

import com.example.Jobportal.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {

    // All active jobs for the public job board
    List<JobEntity> findByIsActiveTrue();

    // All jobs posted by a specific recruiter (for recruiter dashboard)
    List<JobEntity> findByPostedById(Long recruiterId);

    // All jobs belonging to a company
    List<JobEntity> findByCompanyId(Long companyId);

    // ── AI PRE-FILTER: Jobs matching candidate's experience + skill overlap ───
    // When a candidate visits "Recommended Jobs", we first narrow jobs via:
    //   1. experienceRequired <= candidate's yearsOfExperience
    //   2. At least one required skill matches candidate's skills
    // Only THEN do we run the AI to rank these filtered jobs by match score.
    @Query("""
        SELECT DISTINCT j FROM JobEntity j
        JOIN j.requiredSkills s
        WHERE j.isActive = true
        AND j.experienceRequired <= :candidateExperience
        AND s.id IN :candidateSkillIds
    """)
    List<JobEntity> findJobsMatchingCandidate(
            @Param("candidateExperience") Integer candidateExperience,
            @Param("candidateSkillIds") List<Long> candidateSkillIds
    );

    // Eager-fetch requiredSkills with the job in one query to avoid N+1
    // when building the match DTO for the AI scoring service
    @Query("SELECT j FROM JobEntity j LEFT JOIN FETCH j.requiredSkills WHERE j.id = :id")
    Optional<JobEntity> findByIdWithSkills(@Param("id") Long id);

    // Full-text keyword search on title and description — used as a fallback
    // when the AI service is unavailable or for keyword-based filtering
    @Query("""
        SELECT j FROM JobEntity j
        WHERE j.isActive = true
        AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<JobEntity> searchByKeyword(@Param("keyword") String keyword);
}