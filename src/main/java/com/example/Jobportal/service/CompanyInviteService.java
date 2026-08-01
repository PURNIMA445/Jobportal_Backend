package com.example.Jobportal.service;

import java.util.Map;

public interface CompanyInviteService {
    void sendInvite(Long userId, Long companyId, String email);
    Map<String, Object> validateInvite(String token);
    void acceptInvite(Long userId, String token);
    Object getMembers(Long userId, Long companyId);
    void removeMember(Long userId, Long companyId, Long memberId);
}
