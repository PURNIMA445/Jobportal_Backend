package com.example.Jobportal.service;

import com.example.Jobportal.entity.CompanyEntity;
import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.model.RecruiterProfile;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import com.example.Jobportal.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RecruiterProfileServiceImpl implements RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public RecruiterProfileServiceImpl(RecruiterProfileRepository recruiterProfileRepository,
                                       UserRepository userRepository,
                                       CompanyRepository companyRepository) {
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    // WHY the old impl crashed:
    //   entity.getUserId()      → new entity has no userId column; user is @OneToOne UserEntity
    //   entity.getCompanyName() → new entity has no companyName column; company is @ManyToOne
    //   BeanUtils.copyProperties → skips both relations silently → null FK → DB crash
    //
    // Fix: fetch UserEntity and CompanyEntity explicitly, set them as object references.
    @Override
    @Transactional
    public RecruiterProfile createProfile(Long userId, Long companyId, RecruiterProfile profile) {
        if (recruiterProfileRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A recruiter profile already exists for user id: " + userId);
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found: " + userId));

        CompanyEntity companyEntity = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Company not found: " + companyId));

        RecruiterProfileEntity entity = new RecruiterProfileEntity();
        entity.setUser(companyEntity != null ? entity.getUser() : null);
        entity.setUser(userEntity);                  // ← correct OneToOne relation mapping
        entity.setCompany(companyEntity);            // ← correct ManyToOne relation mapping
        entity.setDesignation(profile.getDesignation());
        entity.setPhoneNumber(profile.getPhoneNumber());
        entity.setLinkedinUrl(profile.getLinkedInUrl());

        return toModel(recruiterProfileRepository.save(entity));
    }

    @Override
    public RecruiterProfile getProfileById(Long id) {
        return toModel(findEntityById(id));
    }

    @Override
    public RecruiterProfile getProfileByUserId(Long userId) {
        RecruiterProfileEntity entity = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No recruiter profile found for user id: " + userId));
        return toModel(entity);
    }

    @Override
    public List<RecruiterProfile> getAllProfiles() {
        return recruiterProfileRepository.findAll().stream().map(this::toModel).toList();
    }

    @Override
    public List<RecruiterProfile> getProfilesByCompany(Long companyId) {
        return recruiterProfileRepository.findByCompanyId(companyId)
                .stream().map(this::toModel).toList();
    }

    @Override
    @Transactional
    public RecruiterProfile updateProfile(Long id, RecruiterProfile profile) {
        RecruiterProfileEntity entity = findEntityById(id);
        entity.setDesignation(profile.getDesignation());
        entity.setPhoneNumber(profile.getPhoneNumber());
        entity.setLinkedinUrl(profile.getLinkedInUrl());
        return toModel(recruiterProfileRepository.save(entity));
    }

    // ── ASSIGN TO COMPANY ─────────────────────────────────────────────────────
    // WHY: A recruiter might switch companies. This lets us update the ManyToOne
    // relation properly by fetching the new CompanyEntity and setting it.
    @Override
    @Transactional
    public RecruiterProfile assignToCompany(Long profileId, Long companyId) {
        RecruiterProfileEntity entity = findEntityById(profileId);
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Company not found: " + companyId));
        entity.setCompany(company);
        return toModel(recruiterProfileRepository.save(entity));
    }

    @Override
    @Transactional
    public boolean deleteProfile(Long id) {
        recruiterProfileRepository.delete(findEntityById(id));
        return true;
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────
    private RecruiterProfileEntity findEntityById(Long id) {
        return recruiterProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Recruiter profile not found with id: " + id));
    }

    // WHY explicit mapping: entity.getUser() and entity.getCompany() are lazy-loaded
    // JPA relations. BeanUtils would either miss them or cause LazyInitializationException.
    // We extract only the fields the response model needs.
    private RecruiterProfile toModel(RecruiterProfileEntity entity) {
        return new RecruiterProfile(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getUser() != null ? entity.getUser().getFullName() : null,
                entity.getCompany() != null ? entity.getCompany().getId() : null,
                entity.getCompany() != null ? entity.getCompany().getName() : null,
                entity.getDesignation(),
                null, // jobTitle — not on new entity; remove from model or add field to entity
                null, // specialization — same; keep for backward compat, returns null for now
                entity.getCompany() != null ? entity.getCompany().getWebsite() : null,
                entity.getLinkedinUrl(),
                entity.getPhoneNumber()
        );
    }
}