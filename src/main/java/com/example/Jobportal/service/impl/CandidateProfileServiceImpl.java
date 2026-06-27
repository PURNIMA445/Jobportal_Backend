package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.CandidateProfileRequest;
import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.entity.ProjectEntity;
import com.example.Jobportal.entity.SkillEntity;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.model.CandidateProfileResponse;
import com.example.Jobportal.repository.CandidateProfileRepository;
import com.example.Jobportal.repository.SkillRepository;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public CandidateProfileResponse createProfile(Long userId,
                                                  CandidateProfileRequest request) {
        // 1. Check profile doesn't already exist
        if (candidateProfileRepository.existsByUserId(userId)) {
            throw new RuntimeException("Profile already exists for this user");
        }

        // 2. Get the user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Resolve skills from IDs
        List<SkillEntity> skills = resolveSkills(request.getSkillIds());

        // 4. Build the profile entity
        CandidateProfileEntity profile = CandidateProfileEntity.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .location(request.getLocation())
                .bio(request.getBio())
                .experienceYears(request.getExperienceYears())
                .skills(skills)
                .build();

        // 5. Build and attach projects
        if (request.getProjects() != null) {
            List<ProjectEntity> projects = buildProjects(request.getProjects(), profile);
            profile.setProjects(projects);
        }

        // 6. Save — cascade saves projects too
        CandidateProfileEntity saved = candidateProfileRepository.save(profile);

        return toResponse(saved);
    }

    @Override
    public CandidateProfileResponse getProfile(Long userId) {
        CandidateProfileEntity profile = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toResponse(profile);
    }

    @Override
    @Transactional
    public CandidateProfileResponse updateProfile(Long userId,
                                                  CandidateProfileRequest request) {
        CandidateProfileEntity profile = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // update fields
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setBio(request.getBio());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setSkills(resolveSkills(request.getSkillIds()));

        // replace projects
        if (request.getProjects() != null) {
            profile.getProjects().clear();
            List<ProjectEntity> projects = buildProjects(request.getProjects(), profile);
            profile.getProjects().addAll(projects);
        }

        CandidateProfileEntity saved = candidateProfileRepository.save(profile);
        return toResponse(saved);
    }

    @Override
    public List<CandidateProfileResponse> getAllProfiles() {
        return candidateProfileRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private List<SkillEntity> resolveSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) return new ArrayList<>();
        return skillIds.stream()
                .map(id -> skillRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Skill not found: " + id)))
                .collect(Collectors.toList());
    }

    private List<ProjectEntity> buildProjects(
            List<CandidateProfileRequest.ProjectRequest> projectRequests,
            CandidateProfileEntity profile) {

        return projectRequests.stream()
                .map(pr -> ProjectEntity.builder()
                        .candidate(profile)
                        .title(pr.getTitle())
                        .description(pr.getDescription())
                        .techStack(pr.getTechStack())
                        .projectUrl(pr.getProjectUrl())
                        .complexity(pr.getComplexity())
                        .build())
                .collect(Collectors.toList());
    }

    private CandidateProfileResponse toResponse(CandidateProfileEntity profile) {
        List<CandidateProfileResponse.SkillResponse> skillResponses = profile.getSkills()
                .stream()
                .map(s -> CandidateProfileResponse.SkillResponse.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .category(s.getCategory())
                        .build())
                .collect(Collectors.toList());

        List<CandidateProfileResponse.ProjectResponse> projectResponses = profile.getProjects()
                .stream()
                .map(p -> CandidateProfileResponse.ProjectResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .techStack(p.getTechStack())
                        .projectUrl(p.getProjectUrl())
                        .complexity(p.getComplexity())
                        .build())
                .collect(Collectors.toList());

        return CandidateProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .resumeUrl(profile.getResumeUrl())
                .profilePicUrl(profile.getProfilePicUrl())
                .experienceYears(profile.getExperienceYears())
                .skills(skillResponses)
                .projects(projectResponses)
                .build();
    }
}