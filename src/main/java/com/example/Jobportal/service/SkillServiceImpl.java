package com.example.Jobportal.service;
import com.example.Jobportal.entity.SkillEntity;
import com.example.Jobportal.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }
    @Override
    public SkillEntity saveSkill(SkillEntity skill) {
        return skillRepository.save(skill); // stores skill in DB
    }
    @Override
    public List<SkillEntity> getAllSkills() {
        return skillRepository.findAll(); // fetch all skills from DB
    }
}
