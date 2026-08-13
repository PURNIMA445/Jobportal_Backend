package com.example.Jobportal.service;

import com.example.Jobportal.dto.CompanyRequest;
import com.example.Jobportal.model.CompanyResponse;
import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(Long userId, CompanyRequest request);
    CompanyResponse getCompany(Long id);
    List<CompanyResponse> searchCompanies(String name);
    List<com.example.Jobportal.model.RecruiterProfileResponse> getPendingMembers(Long userId, Long companyId);
    void verifyMember(Long userId, Long companyId, Long memberId, boolean isApproved);
}