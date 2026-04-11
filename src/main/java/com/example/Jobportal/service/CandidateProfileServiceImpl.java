package com.example.Jobportal.service;

import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.entity.SkillEntity;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.model.CandidateProfile;
import com.example.Jobportal.repository.CandidateProfileRepository;
import com.example.Jobportal.repository.SkillRepository;
import com.example.Jobportal.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public CandidateProfileServiceImpl(CandidateProfileRepository candidateProfileRepository,
                                       UserRepository userRepository,
                                       SkillRepository skillRepository) {
        this.candidateProfileRepository = candidateProfileRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    // WHY: The old impl used BeanUtils.copyProperties(profile, entity) which
    // CANNOT copy the OneToOne UserEntity relation — it's not a simple field.
    // Result: user_id column was always null → DB FK constraint crash.
    //
    // Fix: We explicitly fetch the UserEntity by userId and call entity.setUser(userEntity).
    // This is the ONLY correct way to set a JPA relation.
    @Override
    @Transactional
    public CandidateProfile createProfile(Long userId, CandidateProfile profile) {
        if (candidateProfileRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A profile already exists for user id: " + userId);
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + userId));

        CandidateProfileEntity entity = new CandidateProfileEntity();
        entity.setUser(userEntity);                              // ← correct relation mapping
        entity.setResumeText(profile.getResumeText());
        entity.setSummary(profile.getSummary());
        entity.setYearsOfExperience(profile.getYearsOfExperience());
        entity.setLocation(profile.getLocation());
        entity.setPhoneNumber(profile.getPhoneNumber());
        entity.setLinkedinUrl(profile.getLinkedinUrl());
        entity.setPortfolioUrl(profile.getPortfolioUrl());

        return toModel(candidateProfileRepository.save(entity));
    }

    @Override
    public CandidateProfile getProfileById(Long id) {
        return toModel(findEntityById(id));
    }

    // ── GET BY USER ID ────────────────────────────────────────────────────────
    // WHY: When a candidate logs in, we don't know their profileId — we only
    // know their userId from the JWT token. This lets us fetch their profile directly.
    @Override
    public CandidateProfile getProfileByUserId(Long userId) {
        CandidateProfileEntity entity = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No profile found for user id: " + userId));
        return toModel(entity);
    }

    @Override
    public List<CandidateProfile> getAllProfiles() {
        return candidateProfileRepository.findAll()
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional
    public CandidateProfile updateProfile(Long id, CandidateProfile profile) {
        CandidateProfileEntity entity = findEntityById(id);
        entity.setResumeText(profile.getResumeText());
        entity.setSummary(profile.getSummary());
        entity.setYearsOfExperience(profile.getYearsOfExperience());
        entity.setLocation(profile.getLocation());
        entity.setPhoneNumber(profile.getPhoneNumber());
        entity.setLinkedinUrl(profile.getLinkedinUrl());
        entity.setPortfolioUrl(profile.getPortfolioUrl());
        return toModel(candidateProfileRepository.save(entity));
    }

    @Override
    @Transactional
    public boolean deleteProfile(Long id) {
        candidateProfileRepository.delete(findEntityById(id));
        return true;
    }

    // ── ADD SKILL (M2M) ───────────────────────────────────────────────────────
    // WHY: Skills are a ManyToMany relation — you cannot set them via BeanUtils
    // or by passing a list of strings. You must fetch the SkillEntity and call
    // the bidirectional sync helper addSkill() that we put on CandidateProfileEntity.
    // @Transactional is required because we're modifying a lazy-loaded collection.
    @Override
    @Transactional
    public CandidateProfile addSkill(Long candidateId, Long skillId) {
        CandidateProfileEntity candidate = candidateProfileRepository
                .findByIdWithSkills(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Candidate profile not found: " + candidateId));

        SkillEntity skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Skill not found: " + skillId));

        candidate.addSkill(skill);  // bidirectional sync — keeps both sides in sync
        return toModel(candidateProfileRepository.save(candidate));
    }

    @Override
    @Transactional
    public CandidateProfile removeSkill(Long candidateId, Long skillId) {
        CandidateProfileEntity candidate = candidateProfileRepository
                .findByIdWithSkills(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Candidate profile not found: " + candidateId));

        SkillEntity skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Skill not found: " + skillId));

        candidate.removeSkill(skill);
        return toModel(candidateProfileRepository.save(candidate));
    }

    // ── AI HOOK ───────────────────────────────────────────────────────────────
    // WHY: Your Python FastAPI service will call a Spring endpoint like:
    //   GET /api/ai/candidates-for-scoring
    // The controller will call this method and return the raw entities (not models)
    // because Python needs resumeText and the profile id — not the full response model.
    // Python then:
    //   1. Encodes resumeText → vector embedding
    //   2. Compares against job description embedding
    //   3. POSTs the score back to: PUT /api/applications/{id}/match-score
    @Override
    public List<CandidateProfileEntity> getCandidatesForAIScoring() {
        return candidateProfileRepository.findCandidatesWithResume();
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────
    private CandidateProfileEntity findEntityById(Long id) {
        return candidateProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Candidate profile not found with id: " + id));
    }

    // WHY explicit toModel() instead of BeanUtils:
    // entity.getSkills() is a Set<SkillEntity> — BeanUtils cannot convert this
    // to Set<String>. We extract just the skill names for a clean API response.
    // entity.getUser() is a lazy-loaded OneToOne — BeanUtils would trigger an
    // extra DB query and potentially cause a circular serialization issue.
    private CandidateProfile toModel(CandidateProfileEntity entity) {
        return new CandidateProfile(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getResumeText(),
                entity.getSummary(),
                entity.getYearsOfExperience(),
                entity.getLocation(),
                entity.getPhoneNumber(),
                entity.getLinkedinUrl(),
                entity.getPortfolioUrl(),
                entity.getSkills() != null
                        ? entity.getSkills().stream()
                        .map(SkillEntity::getName)
                        .collect(Collectors.toSet())
                        : null
        );
    }
}