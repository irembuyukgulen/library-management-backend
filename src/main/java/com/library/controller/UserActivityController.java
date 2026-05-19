package com.library.controller;

import com.library.dto.UserActivityResponse;
import com.library.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Kullanıcı aktivite geçmişi endpoint'lerini sunan Controller.
 * Endpoint'ler:
 * GET /api/admin/users/{id}/activity → Admin — herhangi kullanıcının aktivitesi
 * GET /api/users/{id}/activity       → Üye — kendi aktivitesi
 * Not: Üyenin başka kullanıcının aktivitesini görememesi
 * için Service katmanında veya @PreAuthorize ile kontrol eklenebilir.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService userActivityService;

    /**
     * Admin herhangi bir kullanıcının aktivite geçmişini görür.
     */
    @GetMapping("/admin/users/{id}/activity")
    public ResponseEntity<UserActivityResponse> getUserActivityAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userActivityService.getUserActivity(id));
    }

    /**
     * Üye kendi aktivite geçmişini görür.
     */
    @GetMapping("/users/{id}/activity")
    public ResponseEntity<UserActivityResponse> getUserActivity(@PathVariable Long id) {
        return ResponseEntity.ok(userActivityService.getUserActivity(id));
    }
}