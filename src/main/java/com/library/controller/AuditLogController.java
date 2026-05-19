package com.library.controller;

import com.library.entity.AuditLog;
import com.library.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Audit log (denetim kaydı) endpoint'lerini sunan Controller.
 * Tüm endpoint'ler ADMIN'e özel.
 * Endpoint'ler:
 * GET /api/admin/audit-logs                       → Son 50 log
 * GET /api/admin/audit-logs/user/{email}          → Kullanıcıya göre loglar
 * GET /api/admin/audit-logs/entity/{type}         → Entity tipine göre loglar
 * GET /api/admin/audit-logs/entity/{type}/{id}    → Belirli kayda ait loglar
 */
@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * Son 50 audit log kaydını getirir.
     * Admin dashboard'da son işlemler için.
     */
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    /**
     * Belirli kullanıcının loglarını getirir.
     */
    @GetMapping("/user/{email}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String email) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(email));
    }

    /**
     * Belirli entity tipinin loglarını getirir.
     */
    @GetMapping("/entity/{type}")
    public ResponseEntity<List<AuditLog>> getLogsByEntityType(@PathVariable String type) {
        return ResponseEntity.ok(auditLogService.getLogsByEntityType(type));
    }

    /**
     * Belirli bir kaydın tüm loglarını getirir.
     */
    @GetMapping("/entity/{type}/{id}")
    public ResponseEntity<List<AuditLog>> getLogsByEntity(
            @PathVariable String type,
            @PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.getLogsByEntity(type, id));
    }
}