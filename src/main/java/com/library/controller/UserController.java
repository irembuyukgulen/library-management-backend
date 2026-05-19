package com.library.controller;

import com.library.dto.UserResponse;
import com.library.dto.UpdateProfileRequest;
import com.library.dto.UpdatePasswordRequest;
import com.library.service.AuditLogService;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;

/**
 * Kullanıcı yönetimi endpoint'lerini sunan Controller.
 * Yetkilendirme:
 * /api/admin/** → Sadece ADMIN (SecurityConfig'de tanımlı)
 * /api/users/** → Giriş yapmış herkes
 * Endpoint'ler:
 * GET    /api/admin/users           → Tüm kullanıcılar (ADMIN)
 * GET    /api/admin/users/{id}      → Kullanıcı detayı (ADMIN)
 * PUT    /api/admin/users/{id}/ban  → Kullanıcı yasakla (ADMIN)
 * PUT    /api/admin/users/{id}/unban → Yasak kaldır (ADMIN)
 * DELETE /api/admin/users/{id}      → Kullanıcı sil (ADMIN)
 * GET    /api/users/me              → Kendi profili (giriş yapılmış)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuditLogService auditLogService;

    /**
     * Profil güncelle — kullanıcı kendi profilini güncelleyebilir.
     */
    @PutMapping("/users/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    /**
     * Şifre güncelle — kullanıcı kendi şifresini değiştirebilir.
     */
    @PutMapping("/users/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin kendi profilini güncelleyebilir.
     */
    @PutMapping("/admin/users/{id}/profile")
    public ResponseEntity<UserResponse> updateProfileAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    /**
     * Admin şifre güncelleme.
     */
    @PutMapping("/admin/users/{id}/password")
    public ResponseEntity<Void> updatePasswordAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Tüm kullanıcıları listeler.
     * Admin panelinde kullanıcı yönetimi için kullanılır.
     */
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * ID'ye göre kullanıcı detayını getirir.
     */
    @GetMapping("/admin/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Giriş yapmış kullanıcının kendi profilini getirir.
     * Authentication → JwtFilter'ın set ettiği kullanıcı bilgisi.
     * authentication.getName() → JWT'den alınan email.
     */
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
    }

    /**
     * Kullanıcıyı yasaklar.
     * Body: { "reason": "Yasaklama sebebi" }
     */
    @PutMapping("/admin/users/{id}/ban")
    public ResponseEntity<UserResponse> banUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        String reason = body.getOrDefault("reason", "Sebep belirtilmedi");
        UserResponse response = userService.banUser(id, reason);

        // Audit log
        auditLogService.log(
                authentication.getName(),
                "USER_BANNED",
                "User",
                id,
                "Kullanıcı yasaklandı. Sebep: " + reason
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Kullanıcının yasağını kaldırır.
     */
    @PutMapping("/admin/users/{id}/unban")
    public ResponseEntity<UserResponse> unbanUser(
            @PathVariable Long id,
            Authentication authentication) {

        UserResponse response = userService.unbanUser(id);

        auditLogService.log(
                authentication.getName(),
                "USER_UNBANNED",
                "User",
                id,
                "Kullanıcı yasağı kaldırıldı"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Kullanıcıyı siler.
     */
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        userService.deleteUser(id);

        auditLogService.log(
                authentication.getName(),
                "USER_DELETED",
                "User",
                id,
                "Kullanıcı silindi"
        );

        return ResponseEntity.noContent().build();
    }
}