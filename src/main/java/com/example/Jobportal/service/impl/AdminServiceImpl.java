package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.*;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.repository.*;
import com.example.Jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;
    private final com.example.Jobportal.utils.EmailUtils emailUtils;
    private final RecruiterProfileRepository recruiterProfileRepository;

    private void verifyAdmin(Long userId) {
        if (userId == null) {
            throw new RuntimeException("Access denied");
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }
    }

    @Override
    public List<SkillEntity> getAllSkills(Long adminId) {
        verifyAdmin(adminId);
        return skillRepository.findAll();
    }

    @Override
    @Transactional
    public SkillEntity createSkill(Long adminId, String name, String category) {
        verifyAdmin(adminId);
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Skill name is required");
        }
        if (skillRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new RuntimeException("Skill already exists");
        }
        SkillEntity skill = SkillEntity.builder()
                .name(name.trim())
                .category(category.trim())
                .build();
        return skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void deleteSkill(Long adminId, Long skillId) {
        verifyAdmin(adminId);
        if (!skillRepository.existsById(skillId)) {
            throw new RuntimeException("Skill not found");
        }
        skillRepository.deleteById(skillId);
    }




    @Override
    public List<Map<String, Object>> getAllJobs(Long adminId) {
        verifyAdmin(adminId);
        List<JobEntity> jobs = jobRepository.findAll();
        return jobs.stream().map(job -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", job.getId());
            map.put("title", job.getTitle());
            map.put("status", job.getStatus() != null ? job.getStatus().name() : "UNKNOWN");
            map.put("companyName", job.getCompany() != null ? job.getCompany().getName() : "Unknown");
            map.put("createdAt", job.getCreatedAt() != null ? job.getCreatedAt().toString() : "");
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteJob(Long adminId, Long jobId) {
        verifyAdmin(adminId);
        if (!jobRepository.existsById(jobId)) {
            throw new RuntimeException("Job not found");
        }
        jobRepository.deleteById(jobId);
    }

    @Override
    public List<Map<String, Object>> getAllApplications(Long adminId) {
        verifyAdmin(adminId);
        List<ApplicationEntity> applications = applicationRepository.findAll();
        return applications.stream().map(app -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", app.getId());
            map.put("candidateEmail", app.getCandidate() != null && app.getCandidate().getUser() != null ? app.getCandidate().getUser().getEmail() : "Unknown");
            map.put("jobTitle", app.getJob() != null ? app.getJob().getTitle() : "Unknown Job");
            map.put("status", app.getStatus() != null ? app.getStatus().name() : "APPLIED");
            map.put("matchScore", app.getMatchScore() != null ? app.getMatchScore() : 0.0);
            map.put("appliedAt", app.getAppliedAt() != null ? app.getAppliedAt().toString() : "");
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteApplication(Long adminId, Long applicationId) {
        verifyAdmin(adminId);
        if (!applicationRepository.existsById(applicationId)) {
            throw new RuntimeException("Application not found");
        }
        applicationRepository.deleteById(applicationId);
    }

}
