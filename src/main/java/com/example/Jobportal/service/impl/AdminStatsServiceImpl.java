package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.repository.ApplicationRepository;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.repository.JobRepository;
import com.example.Jobportal.repository.SkillRepository;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;

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
    public Map<String, Object> getStats(Long adminId) {
        verifyAdmin(adminId);
        long totalUsers = userRepository.count();
        long candidates = userRepository.findAll().stream().filter(u -> u.getRole() == Role.CANDIDATE).count();
        long recruiters = userRepository.findAll().stream().filter(u -> u.getRole() == Role.RECRUITER).count();
        long totalJobs = jobRepository.count();
        long totalSkills = skillRepository.count();
        long totalCompanies = companyRepository.count();
        long totalApplications = applicationRepository.count();

        Map<String, Object> map = new HashMap<>();
        map.put("totalUsers", totalUsers);
        map.put("candidates", candidates);
        map.put("recruiters", recruiters);
        map.put("totalJobs", totalJobs);
        map.put("totalSkills", totalSkills);
        map.put("totalCompanies", totalCompanies);
        map.put("totalApplications", totalApplications);
        return map;
    }
}
