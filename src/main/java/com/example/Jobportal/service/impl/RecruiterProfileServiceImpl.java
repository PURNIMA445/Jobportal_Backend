package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.RecruiterProfileRequest;
import com.example.Jobportal.entity.CompanyEntity;
import com.example.Jobportal.entity.NotificationEntity;
import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.CompanyJoinStatus;
import com.example.Jobportal.enums.CompanyRole;
import com.example.Jobportal.enums.NotifType;
import com.example.Jobportal.model.RecruiterProfileResponse;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.repository.NotificationRepository;
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
    private final NotificationRepository notificationRepository;

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
        CompanyJoinStatus joinStatus = null;
        CompanyRole role = null;
        
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            
            // Default to PENDING and MEMBER when joining an existing company
            joinStatus = CompanyJoinStatus.PENDING;
            role = CompanyRole.MEMBER;
            
            // Send notification to the company owner
            UserEntity owner = userRepository.findById(company.getOwnerId())
                    .orElse(null);
            
            if (owner != null) {
                NotificationEntity notification = NotificationEntity.builder()
                        .user(owner)
                        .message(request.getFullName() + " has requested to join " + company.getName())
                        .type(NotifType.COMPANY_JOIN_REQUEST)
                        .isRead(false)
                        .build();
                notificationRepository.save(notification);
            }
        }

        RecruiterProfileEntity profile = RecruiterProfileEntity.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .designation(request.getDesignation())
                .company(company)
                .companyRole(role)
                .companyJoinStatus(joinStatus)
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
            // Only update company if it's changing
            if (profile.getCompany() == null || !profile.getCompany().getId().equals(request.getCompanyId())) {
                CompanyEntity company = companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() -> new RuntimeException("Company not found"));
                profile.setCompany(company);
                profile.setCompanyRole(CompanyRole.MEMBER);
                profile.setCompanyJoinStatus(CompanyJoinStatus.PENDING);
                
                // Send notification to the new company owner
                UserEntity owner = userRepository.findById(company.getOwnerId())
                        .orElse(null);
                
                if (owner != null) {
                    NotificationEntity notification = NotificationEntity.builder()
                            .user(owner)
                            .message(profile.getFullName() + " has requested to join " + company.getName())
                            .type(NotifType.COMPANY_JOIN_REQUEST)
                            .isRead(false)
                            .build();
                    notificationRepository.save(notification);
                }
            }
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
                .companyRole(profile.getCompanyRole() != null ? profile.getCompanyRole().name() : null)
                .companyJoinStatus(profile.getCompanyJoinStatus() != null ? profile.getCompanyJoinStatus().name() : null)
                .build();
    }
}