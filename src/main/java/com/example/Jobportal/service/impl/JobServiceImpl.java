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
    @Override
    @Transactional
    public JobResponse createJob(Long userId, JobRequest request) {
        RecruiterProfileEntity recruiter = recruiterProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        CompanyEntity company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

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
                .status(job.getStatus())
                .company(companyService.toResponse(job.getCompany()))
                .recruiterName(job.getRecruiter().getFullName())
                .requiredSkills(skillResponses)
                .createdAt(job.getCreatedAt())
                .build();
    }
}