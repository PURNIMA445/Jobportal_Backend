package com.example.Jobportal.controller;
import com.example.Jobportal.entity.SkillEntity;
import com.example.Jobportal.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/skills/{id}")
    public ResponseEntity<SkillEntity> getSkillById(@PathVariable Long id)
    {
        SkillEntity skill;
        skill = skillService.getSkillById(id);
        return ResponseEntity.ok(skill);
    }
    @DeleteMapping("/skills/{id}")
    public  ResponseEntity<Map<String,Boolean>> deleteSkill(@PathVariable Long id)
    {
        boolean deleted;
        deleted=skillService.deleteSkill(id);
        Map<String,Boolean> response= new HashMap<>();
        response.put("deleted",deleted);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/skills/{id}")
    public ResponseEntity<SkillEntity>updateSkill(@PathVariable Long id,
                                        @RequestBody SkillEntity skill){
        skill=skillService.updateSkill(id,skill);
        return ResponseEntity.ok(skill);

    }
}
