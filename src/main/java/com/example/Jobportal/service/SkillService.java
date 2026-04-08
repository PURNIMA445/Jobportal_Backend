package com.example.Jobportal.service;

import com.example.Jobportal.entity.SkillEntity;

import java.util.List;

public interface SkillService {
    SkillEntity saveSkill(SkillEntity skill); // for POST
    List<SkillEntity> getAllSkills();
    SkillEntity getSkillById(Long id);

    boolean deleteSkill(Long id);

    SkillEntity updateSkill(Long id, SkillEntity skill);
}
