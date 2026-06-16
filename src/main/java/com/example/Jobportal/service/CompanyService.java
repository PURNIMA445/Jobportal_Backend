package com.example.Jobportal.service;

import com.example.Jobportal.dto.CompanyRequest;
import com.example.Jobportal.model.CompanyResponse;
import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(CompanyRequest request);
    CompanyResponse getCompany(Long id);
    List<CompanyResponse> searchCompanies(String name);
}