package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.RecruiterProfileRequest;
import com.example.Jobportal.entity.CompanyEntity;
import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.model.RecruiterProfileResponse;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.RecruiterProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruiterProfileServiceImpl implements RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyServiceImpl companyService;

    @Override
    @Transactional
    public RecruiterProfileResponse createProfile(Long userId,
                                                  RecruiterProfileRequest request) {
        if (recruiterProfileRepository.existsByUserId(userId)) {
            throw new RuntimeException("Profile already exists");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CompanyEntity company = null;
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
        }

        RecruiterProfileEntity profile = RecruiterProfileEntity.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .designation(request.getDesignation())
                .company(company)
                .build();

        return toResponse(recruiterProfileRepository.save(profile));
    }

    @Override
    public RecruiterProfileResponse getProfile(Long userId) {
        RecruiterProfileEntity profile = recruiterProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toResponse(profile);
    }

    @Override
    @Transactional
    public RecruiterProfileResponse updateProfile(Long userId,
                                                  RecruiterProfileRequest request) {
        RecruiterProfileEntity profile = recruiterProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDesignation(request.getDesignation());

        if (request.getCompanyId() != null) {
            CompanyEntity company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            profile.setCompany(company);
        }

        return toResponse(recruiterProfileRepository.save(profile));
    }

    private RecruiterProfileResponse toResponse(RecruiterProfileEntity profile) {
        return RecruiterProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .designation(profile.getDesignation())
                .company(profile.getCompany() != null
                        ? companyService.toResponse(profile.getCompany())
                        : null)
                .build();
    }
}