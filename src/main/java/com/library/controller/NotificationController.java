package com.library.controller;

import com.library.dto.NotificationResponse;
import com.library.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kira süresi bildirimleri endpoint'lerini sunan Controller.
 * Kullanıcı giriş yaptığında frontend bu endpoint'i çağırır.
 * Süresi yaklaşan veya geçmiş kiralamalar bildirim olarak döner.
 * Endpoint'ler:
 * GET /api/notifications/user/{userId} → Kullanıcının bildirimleri
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Kullanıcının kira süresi bildirimlerini getirir.
     */
    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }
}