package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.JobRequest;
import com.example.Jobportal.entity.*;
import com.example.Jobportal.enums.JobStatus;
import com.example.Jobportal.model.CandidateProfileResponse;
import com.example.Jobportal.model.JobResponse;
import com.example.Jobportal.repository.*;
import com.example.Jobportal.service.JobService;
import com.example.Jobportal.service.NotificationService;
import com.example.Jobportal.service.impl.CompanyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final CompanyServiceImpl companyService;
    private final NotificationService notificationService;
    private final CandidateProfileRepository candidateProfileRepository;

    @Override
    public List<JobResponse> getRecommendedJobs(Long userId) {
        CandidateProfileEntity candidate = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        List<Long> skillIds = candidate.getSkills().stream()
                .map(SkillEntity::getId)
                .collect(Collectors.toList());

        if (skillIds.isEmpty()) {
            return new ArrayList<>();
        }

        return jobRepository.findRecommendedJobs(skillIds)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public JobResponse createJob(Long userId, JobRequest request) {
        RecruiterProfileEntity recruiter = recruiterProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        CompanyEntity company = recruiter.getCompany();
        if (company == null) {
            throw new RuntimeException("You are not linked to any company. Please set up your company profile first.");
        }

        if (company.getStatus() != com.example.Jobportal.enums.CompanyStatus.APPROVED) {
            throw new RuntimeException("Your company is pending admin verification or has been rejected");
        }

        List<SkillEntity> skills = new ArrayList<>();
        if (request.getRequiredSkillIds() != null) {
            skills = request.getRequiredSkillIds().stream()
                    .map(id -> skillRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Skill not found: " + id)))
                    .collect(Collectors.toList());
        }

        JobEntity job = JobEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .experienceLevel(request.getExperienceLevel())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .responsibilities(request.getResponsibilities())
                .requirements(request.getRequirements())
                .benefits(request.getBenefits())
                .company(company)
                .recruiter(recruiter)
                .requiredSkills(skills)
                .build();


        JobEntity saved = jobRepository.save(job);
        notificationService.sendJobMatchNotifications(saved.getId());
        return toResponse(saved);
    }

    @Override
    public JobResponse getJob(Long id) {
        return toResponse(jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found")));
    }

    @Override
    public List<JobResponse> getAllOpenJobs() {
        return jobRepository.findByStatus(JobStatus.OPEN)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> getJobsByRecruiter(Long userId) {
        RecruiterProfileEntity recruiter = recruiterProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
        return jobRepository.findByRecruiterId(recruiter.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> searchJobs(String keyword) {
        return jobRepository.searchByKeyword(keyword)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobResponse closeJob(Long jobId, Long userId) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only close your own jobs");
        }

        job.setStatus(JobStatus.CLOSED);
        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, Long userId, JobRequest request) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own jobs");
        }

        List<SkillEntity> skills = new ArrayList<>();
        if (request.getRequiredSkillIds() != null) {
            skills = request.getRequiredSkillIds().stream()
                    .map(id -> skillRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Skill not found: " + id)))
                    .collect(Collectors.toList());
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setResponsibilities(request.getResponsibilities());
        job.setRequirements(request.getRequirements());
        job.setBenefits(request.getBenefits());
        job.setRequiredSkills(skills);

        return toResponse(jobRepository.save(job));
    }

    public JobResponse toResponse(JobEntity job) {
        List<CandidateProfileResponse.SkillResponse> skillResponses = job.getRequiredSkills()
                .stream()
                .map(s -> CandidateProfileResponse.SkillResponse.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .category(s.getCategory())
                        .build())
                .collect(Collectors.toList());

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .responsibilities(job.getResponsibilities())
                .requirements(job.getRequirements())
                .benefits(job.getBenefits())
                .status(job.getStatus())
                .company(companyService.toResponse(job.getCompany()))
                .recruiterName(job.getRecruiter().getFullName())
                .requiredSkills(skillResponses)
                .createdAt(job.getCreatedAt())
                .build();
    }
}