package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.CompanyEntity;
import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import com.example.Jobportal.repository.UserRepository;
import com.example.Jobportal.service.AdminCompanyService;
import com.example.Jobportal.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCompanyServiceImpl implements AdminCompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final EmailUtils emailUtils;

    private void verifyAdmin(Long userId) {
        if (userId == null) {
            throw new RuntimeException("Access denied");
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }
    }

    @Override
    public List<CompanyEntity> getAllCompanies(Long adminId) {
        verifyAdmin(adminId);
        return companyRepository.findAll();
    }

    @Override
    public List<CompanyEntity> getPendingCompanies(Long adminId) {
        verifyAdmin(adminId);
        return companyRepository.findAll().stream()
                .filter(c -> c.getStatus() == com.example.Jobportal.enums.CompanyStatus.PENDING_VERIFICATION)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void verifyCompany(Long adminId, Long companyId, boolean approve, String reason) {
        verifyAdmin(adminId);
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        if (approve) {
            company.setStatus(com.example.Jobportal.enums.CompanyStatus.APPROVED);
        } else {
            company.setStatus(com.example.Jobportal.enums.CompanyStatus.REJECTED);
            company.setRejectionReason(reason);
        }
        
        companyRepository.save(company);
        
        // Find owner to send email
        if (company.getOwnerId() != null) {
            recruiterProfileRepository.findById(company.getOwnerId()).ifPresent(ownerProfile -> {
                if (ownerProfile.getUser() != null) {
                    emailUtils.sendCompanyVerificationEmail(
                            ownerProfile.getUser().getEmail(),
                            company.getName(),
                            approve,
                            reason
                    );
                }
            });
        }
    }

    @Override
    @Transactional
    public void deleteCompany(Long adminId, Long companyId) {
        verifyAdmin(adminId);
        if (!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company not found");
        }
        companyRepository.deleteById(companyId);
    }
}
