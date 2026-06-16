package com.example.Jobportal.repository;

import com.example.Jobportal.entity.JobEntity;
import com.example.Jobportal.enums.JobStatus;
import com.example.Jobportal.enums.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {

    List<JobEntity> findByStatus(JobStatus status);

    List<JobEntity> findByCompanyId(Long companyId);

    List<JobEntity> findByRecruiterId(Long recruiterId);

    List<JobEntity> findByStatusAndJobType(JobStatus status, JobType jobType);

    @Query("SELECT j FROM JobEntity j WHERE " +
            "j.status = 'OPEN' AND " +
            "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<JobEntity> searchByKeyword(@Param("keyword") String keyword);
}