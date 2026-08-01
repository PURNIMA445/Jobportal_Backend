package com.example.Jobportal.controller;

import com.example.Jobportal.dto.CompanyRequest;
import com.example.Jobportal.service.CompanyService;
import com.example.Jobportal.service.CompanyInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyInviteService inviteService;

    @PostMapping
    public ResponseEntity<?> createCompany(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCompanies(@RequestParam String name) {
        return ResponseEntity.ok(companyService.searchCompanies(name));
    }

    // ═══ INVITES & TEAM MEMBERS ════════════════════════════════════════════

    @PostMapping("/{id}/invite")
    public ResponseEntity<?> sendInvite(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String email = body.get("email");
        inviteService.sendInvite(userId, id, email);
        return ResponseEntity.ok(Map.of("message", "Invite sent successfully"));
    }

    @GetMapping("/invite/validate/{token}")
    public ResponseEntity<?> validateInvite(@PathVariable String token) {
        return ResponseEntity.ok(inviteService.validateInvite(token));
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<?> acceptInvite(
            @AuthenticationPrincipal Long userId,
            @RequestParam String token) {
        inviteService.acceptInvite(userId, token);
        return ResponseEntity.ok(Map.of("message", "Invite accepted successfully"));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<?> getMembers(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(inviteService.getMembers(userId, id));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<?> removeMember(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @PathVariable Long memberId) {
        inviteService.removeMember(userId, id, memberId);
        return ResponseEntity.ok(Map.of("message", "Member removed successfully"));
    }
}