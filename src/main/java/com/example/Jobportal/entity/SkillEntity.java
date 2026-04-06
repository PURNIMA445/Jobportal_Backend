package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="skills")
public class SkillEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String skillName;
    public SkillEntity(long id, String skillName) {
        this.id = id;
        this.skillName = skillName;

    }
    public SkillEntity()
    {

    }
}
