package com.example.Jobportal.service;

import com.example.Jobportal.entity.CompanyEntity;
import com.example.Jobportal.entity.JobEntity;
import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.entity.SkillEntity;
import com.example.Jobportal.model.Job;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.repository.JobRepository;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import com.example.Jobportal.repository.SkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final SkillRepository skillRepository;

    public JobServiceImpl(JobRepository jobRepository,
                          CompanyRepository companyRepository,
                          RecruiterProfileRepository recruiterProfileRepository,
                          SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.skillRepository = skillRepository;
    }

    // ── SAVE ──────────────────────────────────────────────────────────────────
    // WHY the old impl crashed:
    //   jobEntity.getCompanyName() → field doesn't exist — company is @ManyToOne
    //   jobEntity.getSalary()      → entity has salaryMin/salaryMax, not salary
    //   jobEntity.getPostedDate()  → entity has postedAt (LocalDateTime), not postedDate
    //
    // Fix: explicitly fetch CompanyEntity and RecruiterProfileEntity by their IDs
    // and set them with entity.setCompany() / entity.setPostedBy().
    // This is the ONLY correct way to set JPA relations.
    @Override
    @Transactional
    public Job saveJob(Job job, Long companyId, Long recruiterProfileId) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Company not found: " + companyId));

        RecruiterProfileEntity recruiter = recruiterProfileRepository.findById(recruiterProfileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Recruiter profile not found: " + recruiterProfileId));

        JobEntity entity = new JobEntity();
        entity.setTitle(job.getTitle());
        entity.setDescription(job.getDescription());
        entity.setLocation(job.getLocation());
        entity.setJobType(job.getJobType());
        entity.setSalaryMin(job.getSalaryMin());
        entity.setSalaryMax(job.getSalaryMax());
        entity.setExperienceRequired(job.getExperienceRequired());
        entity.setIsActive(true);
        entity.setCompany(company);       // ← correct ManyToOne relation mapping
        entity.setPostedBy(recruiter);    // ← correct ManyToOne relation mapping

        return toModel(jobRepository.save(entity));
    }

    @Override
    public Job getJobById(Long id) {
        return toModel(findEntityById(id));
    }

    @Override
    public JobEntity getJobEntityById(Long id) {
        // Returns raw entity — used by Python AI service to read description text
        return jobRepository.findByIdWithSkills(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Job not found: " + id));
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll().stream().map(this::toModel).toList();
    }

    @Override
    public List<Job> getActiveJobs() {
        return jobRepository.findByIsActiveTrue().stream().map(this::toModel).toList();
    }

    @Override
    public List<Job> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyId(companyId).stream().map(this::toModel).toList();
    }

    @Override
    public List<Job> getJobsByRecruiter(Long recruiterId) {
        return jobRepository.findByPostedById(recruiterId).stream().map(this::toModel).toList();
    }

    @Override
    @Transactional
    public Job updateJob(Long id, Job job) {
        JobEntity entity = findEntityById(id);
        entity.setTitle(job.getTitle());
        entity.setDescription(job.getDescription());
        entity.setLocation(job.getLocation());
        entity.setJobType(job.getJobType());
        entity.setSalaryMin(job.getSalaryMin());
        entity.setSalaryMax(job.getSalaryMax());
        entity.setExperienceRequired(job.getExperienceRequired());
        if (job.getIsActive() != null) entity.setIsActive(job.getIsActive());
        return toModel(jobRepository.save(entity));
    }

    @Override
    @Transactional
    public boolean deleteJob(Long id) {
        jobRepository.delete(findEntityById(id));
        return true;
    }

    // ── ADD REQUIRED SKILL (M2M) ──────────────────────────────────────────────
    // WHY: requiredSkills is @ManyToMany — must be managed through the entity
    // helper method addSkill(), not by setting a list of strings.
    @Override
    @Transactional
    public Job addRequiredSkill(Long jobId, Long skillId) {
        JobEntity job = jobRepository.findByIdWithSkills(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Job not found: " + jobId));
        SkillEntity skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Skill not found: " + skillId));
        job.addSkill(skill);
        return toModel(jobRepository.save(job));
    }

    @Override
    @Transactional
    public Job removeRequiredSkill(Long jobId, Long skillId) {
        JobEntity job = jobRepository.findByIdWithSkills(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Job not found: " + jobId));
        SkillEntity skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Skill not found: " + skillId));
        job.removeSkill(skill);
        return toModel(jobRepository.save(job));
    }

    @Override
    public List<Job> searchByKeyword(String keyword) {
        return jobRepository.searchByKeyword(keyword).stream().map(this::toModel).toList();
    }

    // ── AI PRE-FILTER HOOK ────────────────────────────────────────────────────
    // WHY: Before calling Python's embedding model (which is expensive),
    // we filter jobs with a cheap SQL query: only jobs where
    //   experienceRequired <= candidate's experience AND
    //   at least one required skill overlaps with candidate's skills.
    // Python then only encodes and compares this smaller filtered list.
    @Override
    public List<Job> getJobsMatchingCandidate(Integer yearsOfExperience, List<Long> candidateSkillIds) {
        return jobRepository.findJobsMatchingCandidate(yearsOfExperience, candidateSkillIds)
                .stream()
                .map(this::toModel)
                .toList();
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────
    private JobEntity findEntityById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Job not found with id: " + id));
    }

    private Job toModel(JobEntity entity) {
        return new Job(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getJobType(),
                entity.getSalaryMin(),
                entity.getSalaryMax(),
                entity.getExperienceRequired(),
                entity.getIsActive(),
                entity.getPostedAt(),
                entity.getCompany() != null ? entity.getCompany().getId() : null,
                entity.getCompany() != null ? entity.getCompany().getName() : null,
                entity.getPostedBy() != null ? entity.getPostedBy().getId() : null,
                entity.getRequiredSkills() != null
                        ? entity.getRequiredSkills().stream()
                        .map(SkillEntity::getName)
                        .collect(Collectors.toSet())
                        : null
        );
    }
}