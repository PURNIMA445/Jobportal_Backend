package com.example.Jobportal.service.impl;

import com.example.Jobportal.dto.CompanyRequest;
import com.example.Jobportal.entity.CompanyEntity;
import com.example.Jobportal.model.CompanyResponse;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public CompanyResponse createCompany(CompanyRequest request) {
        if (companyRepository.existsByName(request.getName())) {
            throw new RuntimeException("Company already exists");
        }

        CompanyEntity company = CompanyEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .industry(request.getIndustry())
                .location(request.getLocation())
                .websiteUrl(request.getWebsiteUrl())
                .build();

        return toResponse(companyRepository.save(company));
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
                .createdAt(company.getCreatedAt())
                .build();
    }
}