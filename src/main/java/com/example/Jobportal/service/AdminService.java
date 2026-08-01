package com.example.Jobportal.service;

import com.example.Jobportal.entity.SkillEntity;
import com.example.Jobportal.entity.CompanyEntity;
import java.util.List;
import java.util.Map;

public interface AdminService {
    List<SkillEntity> getAllSkills(Long adminId);
    SkillEntity createSkill(Long adminId, String name, String category);
    void deleteSkill(Long adminId, Long skillId);



    List<Map<String, Object>> getAllJobs(Long adminId);
    void deleteJob(Long adminId, Long jobId);

    List<Map<String, Object>> getAllApplications(Long adminId);
    void deleteApplication(Long adminId, Long applicationId);
}
