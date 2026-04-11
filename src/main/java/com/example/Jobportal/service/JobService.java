package com.example.Jobportal.service;

import com.example.Jobportal.entity.JobEntity;
import com.example.Jobportal.model.Job;

import java.util.List;

public interface JobService {

    Job saveJob(Job job, Long companyId, Long recruiterProfileId); // relations passed as IDs
    Job getJobById(Long id);
    List<Job> getAllJobs();
    List<Job> getActiveJobs();
    List<Job> getJobsByCompany(Long companyId);
    List<Job> getJobsByRecruiter(Long recruiterId);
    Job updateJob(Long id, Job job);
    boolean deleteJob(Long id);

    // ── Skill management (M2M) ────────────────────────────────────────────────
    Job addRequiredSkill(Long jobId, Long skillId);
    Job removeRequiredSkill(Long jobId, Long skillId);

    // ── Search ───────────────────────────────────────────────────────────────
    List<Job> searchByKeyword(String keyword);

    // ── AI hooks ──────────────────────────────────────────────────────────────
    // Python calls GET /api/ai/jobs-for-candidate/{candidateId}
    // Spring pre-filters by experience + skill overlap (cheap SQL),
    // Python then re-ranks those filtered jobs by cosine similarity.
    List<Job> getJobsMatchingCandidate(Integer yearsOfExperience, List<Long> candidateSkillIds);

    // Returns raw entity — Python needs the description text to encode it.
    JobEntity getJobEntityById(Long id);
}