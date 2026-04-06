package com.example.Jobportal.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Jobportal.entity.SkillEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository <SkillEntity,Long> {
}
