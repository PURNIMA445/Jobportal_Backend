package com.example.Jobportal.repository;

import com.example.Jobportal.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<JobEntity,Long> {
}
