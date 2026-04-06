package com.example.Jobportal.service;

import com.example.Jobportal.entity.SkillEntity;

import java.util.List;

public interface SkillService {
    SkillEntity saveSkill(SkillEntity skill); // for POST
    List<SkillEntity> getAllSkills();
}
