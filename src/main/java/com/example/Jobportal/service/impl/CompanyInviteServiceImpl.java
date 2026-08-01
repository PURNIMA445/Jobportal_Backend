package com.example.Jobportal.service.impl;

import com.example.Jobportal.entity.CompanyEntity;
import com.example.Jobportal.entity.CompanyInviteEntity;
import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.enums.CompanyRole;
import com.example.Jobportal.enums.CompanyStatus;
import com.example.Jobportal.repository.CompanyInviteRepository;
import com.example.Jobportal.repository.CompanyRepository;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import com.example.Jobportal.service.CompanyInviteService;
import com.example.Jobportal.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyInviteServiceImpl implements CompanyInviteService {

    private final CompanyInviteRepository inviteRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final EmailUtils emailUtils;

    @Override
    @Transactional
    public void sendInvite(Long userId, Long companyId, String email) {
        RecruiterProfileEntity recruiter = getRecruiter(userId);
        validateAdmin(recruiter, companyId);
        
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
                
        if (company.getStatus() != CompanyStatus.APPROVED) {
            throw new RuntimeException("Company must be verified before sending invites");
        }

        if (inviteRepository.findByCompanyIdAndEmail(companyId, email).filter(i -> "PENDING".equals(i.getStatus())).isPresent()) {
            throw new RuntimeException("Active invite already exists for this email");
        }

        String token = UUID.randomUUID().toString();
        CompanyInviteEntity invite = CompanyInviteEntity.builder()
                .token(token)
                .email(email)
                .company(company)
                .invitedBy(recruiter)
                .status("PENDING")
                .expiresAt(LocalDateTime.now().plusHours(48))
                .build();

        inviteRepository.save(invite);

        String inviteLink = "http://localhost:3000/recruiter/join?token=" + token;
        emailUtils.sendInviteEmail(email, company.getName(), inviteLink);
    }

    @Override
    public Map<String, Object> validateInvite(String token) {
        CompanyInviteEntity invite = getValidInvite(token);
        return Map.of(
                "companyId", invite.getCompany().getId(),
                "companyName", invite.getCompany().getName(),
                "email", invite.getEmail()
        );
    }

    @Override
    @Transactional
    public void acceptInvite(Long userId, String token) {
        RecruiterProfileEntity recruiter = getRecruiter(userId);
        CompanyInviteEntity invite = getValidInvite(token);

        if (!recruiter.getUser().getEmail().equals(invite.getEmail())) {
            throw new RuntimeException("Invite email does not match your account email");
        }

        recruiter.setCompany(invite.getCompany());
        recruiter.setCompanyRole(CompanyRole.MEMBER);
        recruiterProfileRepository.save(recruiter);

        invite.setStatus("ACCEPTED");
        inviteRepository.save(invite);
    }

    @Override
    public List<Map<String, Object>> getMembers(Long userId, Long companyId) {
        RecruiterProfileEntity recruiter = getRecruiter(userId);
        validateAdmin(recruiter, companyId);

        return recruiterProfileRepository.findByCompanyId(companyId).stream()
                .map(r -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", r.getId());
                    map.put("name", r.getFullName());
                    map.put("designation", r.getDesignation());
                    map.put("role", r.getCompanyRole() != null ? r.getCompanyRole().name() : null);
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeMember(Long userId, Long companyId, Long memberId) {
        RecruiterProfileEntity admin = getRecruiter(userId);
        validateAdmin(admin, companyId);

        RecruiterProfileEntity member = recruiterProfileRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!companyId.equals(member.getCompany().getId())) {
            throw new RuntimeException("Member not in this company");
        }

        if (member.getId().equals(admin.getId())) {
            throw new RuntimeException("Cannot remove yourself");
        }

        member.setCompany(null);
        member.setCompanyRole(null);
        recruiterProfileRepository.save(member);
    }

    private RecruiterProfileEntity getRecruiter(Long userId) {
        return recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
    }

    private void validateAdmin(RecruiterProfileEntity recruiter, Long companyId) {
        if (recruiter.getCompany() == null || !recruiter.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("You do not belong to this company");
        }
        if (recruiter.getCompanyRole() != CompanyRole.ADMIN) {
            throw new RuntimeException("Only Company Admin can perform this action");
        }
    }

    private CompanyInviteEntity getValidInvite(String token) {
        CompanyInviteEntity invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invite token"));

        if (!"PENDING".equals(invite.getStatus())) {
            throw new RuntimeException("Invite has already been accepted or expired");
        }
        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            invite.setStatus("EXPIRED");
            inviteRepository.save(invite);
            throw new RuntimeException("Invite has expired");
        }
        return invite;
    }
}
