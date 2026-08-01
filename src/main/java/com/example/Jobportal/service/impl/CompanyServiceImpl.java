package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.CompanyRequest;
import com.example.Jobportal.entity.CompanyEntity;
import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.enums.CompanyRole;
import com.example.Jobportal.enums.CompanyStatus;
import com.example.Jobportal.model.CompanyResponse;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import com.example.Jobportal.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    @Override
    @Transactional
    public CompanyResponse createCompany(Long userId, CompanyRequest request) {
        if (companyRepository.existsByName(request.getName())) {
            throw new RuntimeException("Company already exists");
        }

        RecruiterProfileEntity recruiter = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        CompanyEntity company = CompanyEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .industry(request.getIndustry())
                .location(request.getLocation())
                .websiteUrl(request.getWebsiteUrl())
                .status(CompanyStatus.PENDING_VERIFICATION)
                .ownerId(recruiter.getId())
                .build();

        CompanyEntity savedCompany = companyRepository.save(company);

        // Update recruiter profile to be the ADMIN of the new company
        recruiter.setCompany(savedCompany);
        recruiter.setCompanyRole(CompanyRole.ADMIN);
        recruiterProfileRepository.save(recruiter);

        return toResponse(savedCompany);
    }

    @Override
    public CompanyResponse getCompany(Long id) {
        CompanyEntity company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return toResponse(company);
    }

    @Override
    public List<CompanyResponse> searchCompanies(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse toResponse(CompanyEntity company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .industry(company.getIndustry())
                .location(company.getLocation())
                .websiteUrl(company.getWebsiteUrl())
                .logoUrl(company.getLogoUrl())
                .status(company.getStatus() != null ? company.getStatus().name() : null)
                .createdAt(company.getCreatedAt())
                .build();
    }
}