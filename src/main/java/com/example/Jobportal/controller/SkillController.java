package com.example.Jobportal.controller;

import com.example.Jobportal.entity.SkillEntity;
import com.example.Jobportal.service.SkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;
    // Constructor injection
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }
    @GetMapping("/skills")
    public List<SkillEntity> getAllSkills() {
        return skillService.getAllSkills();
    }
    // POST create a new skill
    @PostMapping("/skills")
    public SkillEntity createSkill(@RequestBody SkillEntity skill) {
        return skillService.saveSkill(skill);
    }
}
