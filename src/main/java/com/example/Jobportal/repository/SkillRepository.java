package com.example.Jobportal.repository;

import com.example.Jobportal.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Long> {
    Optional<SkillEntity> findByNameIgnoreCase(String name);
    List<SkillEntity> findByCategory(String category);
}