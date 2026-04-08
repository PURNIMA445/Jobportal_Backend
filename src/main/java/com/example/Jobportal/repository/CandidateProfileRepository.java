package com.example.Jobportal.repository;

import com.example.Jobportal.entity.CandidateProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfileEntity,Long> {
}
