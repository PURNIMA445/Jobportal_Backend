package com.example.Jobportal.service;
import com.example.Jobportal.entity.SkillEntity;
import com.example.Jobportal.repository.SkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    @Override
    public SkillEntity getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Skill not found with id: " + id));
    }
    @Override
    public boolean deleteSkill(Long id) {
        SkillEntity skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));

        skillRepository.delete(skill);
        return true;
    }

    @Override
    public SkillEntity updateSkill(Long id, SkillEntity skill) {
        SkillEntity skillEntity = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));
        skillEntity.setSkillName(skill.getSkillName());
        skillRepository.save(skillEntity);
        return skillEntity;
    }
}
