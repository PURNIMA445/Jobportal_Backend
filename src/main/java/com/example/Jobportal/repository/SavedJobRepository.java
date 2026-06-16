package com.example.Jobportal.repository;

import com.example.Jobportal.entity.SavedJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJobEntity, Long> {
    List<SavedJobEntity> findByCandidateId(Long candidateId);
    Boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);
    void deleteByCandidateIdAndJobId(Long candidateId, Long jobId);
}