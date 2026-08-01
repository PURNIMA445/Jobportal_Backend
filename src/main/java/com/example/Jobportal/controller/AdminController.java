package com.example.Jobportal.controller;

import com.example.Jobportal.service.AdminCompanyService;
import com.example.Jobportal.service.AdminService;
import com.example.Jobportal.service.AdminStatsService;
import com.example.Jobportal.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminUserService adminUserService;
    private final AdminCompanyService adminCompanyService;
    private final AdminStatsService adminStatsService;

    // ═══ SKILLS ════════════════════════════════════════════════════════════

    @GetMapping("/skills")
    public ResponseEntity<?> getAllSkills(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(adminService.getAllSkills(userId));
    }

    @PostMapping("/skills")
    public ResponseEntity<?> createSkill(
            @AuthenticationPrincipal Long userId,
            @RequestBody Map<String, String> body) {
        String name = body.get("name");
        String category = body.getOrDefault("category", "General");
        return ResponseEntity.ok(adminService.createSkill(userId, name, category));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<?> deleteSkill(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        adminService.deleteSkill(userId, id);
        return ResponseEntity.ok(Map.of("message", "Skill deleted"));
    }

    // ═══ USERS ═════════════════════════════════════════════════════════════

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(adminUserService.getAllUsers(userId));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String newRoleStr = body.get("role");
        adminUserService.updateUserRole(userId, id, newRoleStr);
        return ResponseEntity.ok(Map.of("message", "User role updated successfully to " + newRoleStr));
    }

    @PutMapping("/users/{id}/verify")
    public ResponseEntity<?> toggleUserVerification(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Boolean verify = body.get("isEmailVerified");
        adminUserService.toggleUserVerification(userId, id, verify);
        return ResponseEntity.ok(Map.of("message", "User verification status updated to " + verify));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        adminUserService.deleteUser(userId, id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    // ═══ COMPANIES ════════════════════════════════════════════════════════

    @GetMapping("/companies")
    public ResponseEntity<?> getAllCompanies(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(adminCompanyService.getAllCompanies(userId));
    }

    @GetMapping("/companies/pending")
    public ResponseEntity<?> getPendingCompanies(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(adminCompanyService.getPendingCompanies(userId));
    }

    @PatchMapping("/companies/{id}/verify")
    public ResponseEntity<?> verifyCompany(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        boolean approve = (Boolean) body.get("approve");
        String reason = (String) body.get("reason");
        adminCompanyService.verifyCompany(userId, id, approve, reason);
        return ResponseEntity.ok(Map.of("message", "Company verification updated"));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<?> deleteCompany(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        adminCompanyService.deleteCompany(userId, id);
        return ResponseEntity.ok(Map.of("message", "Company deleted"));
    }

    // ═══ JOBS ══════════════════════════════════════════════════════════════

    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobs(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(adminService.getAllJobs(userId));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<?> deleteJob(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        adminService.deleteJob(userId, id);
        return ResponseEntity.ok(Map.of("message", "Job deleted"));
    }

    // ═══ APPLICATIONS ══════════════════════════════════════════════════════

    @GetMapping("/applications")
    public ResponseEntity<?> getAllApplications(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(adminService.getAllApplications(userId));
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<?> deleteApplication(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        adminService.deleteApplication(userId, id);
        return ResponseEntity.ok(Map.of("message", "Application deleted"));
    }

    // ═══ STATS ═════════════════════════════════════════════════════════════

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(adminStatsService.getStats(userId));
    }
}
