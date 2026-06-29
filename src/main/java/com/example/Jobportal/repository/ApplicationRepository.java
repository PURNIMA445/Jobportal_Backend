package com.example.Jobportal.repository;

import com.example.Jobportal.entity.ApplicationEntity;
import com.example.Jobportal.enums.AppStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    List<ApplicationEntity> findByCandidateId(Long candidateId);

    List<ApplicationEntity> findByJobId(Long jobId);

    List<ApplicationEntity> findByJobIdOrderByRankScoreDesc(Long jobId);

    Optional<ApplicationEntity> findByCandidateIdAndJobId(Long candidateId, Long jobId);

    Boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    @Query("SELECT a FROM ApplicationEntity a WHERE a.job.id = :jobId " +
            "AND a.status = :status ORDER BY a.rankScore DESC")
    List<ApplicationEntity> findByJobIdAndStatus(
            @Param("jobId") Long jobId,
            @Param("status") AppStatus status
    );
    List<ApplicationEntity> findByJobIdIn(List<Long> jobIds);
    void deleteByJobIdIn(List<Long> jobIds);
    void deleteByCandidateId(Long candidateId);
}