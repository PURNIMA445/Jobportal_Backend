package com.example.Jobportal.service;

import com.example.Jobportal.entity.CompanyEntity;

import java.util.List;

public interface AdminCompanyService {
    List<CompanyEntity> getAllCompanies(Long adminId);
    List<CompanyEntity> getPendingCompanies(Long adminId);
    void verifyCompany(Long adminId, Long companyId, boolean approve, String reason);
    void deleteCompany(Long adminId, Long companyId);
}
