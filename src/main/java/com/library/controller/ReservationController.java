package com.library.controller;

import com.library.dto.ReservationRequest;
import com.library.dto.ReservationResponse;
import com.library.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rezervasyon işlemlerini yöneten Controller.
 * Yetkilendirme:
 * /api/admin/reservations/** → Sadece ADMIN
 * /api/reservations/**       → Giriş yapılmış herkes
 * Endpoint'ler:
 * POST /api/reservations                      → Rezervasyon oluştur
 * PUT  /api/reservations/{id}/cancel          → İptal et
 * GET  /api/reservations/user/{userId}        → Kullanıcının rezervasyonları
 * GET  /api/admin/reservations                → Tüm rezervasyonlar (ADMIN)
 * GET  /api/admin/reservations/book/{bookId}  → Kitabın rezervasyonları (ADMIN)
 * PUT  /api/admin/reservations/expire         → Süresi dolmuşları güncelle (ADMIN)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Yeni rezervasyon oluşturur.
     * Hem üye hem admin kullanabilir.
     */
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request));
    }

    /**
     * Rezervasyonu iptal eder.
     * Üye kendi rezervasyonunu, admin herhangi birini iptal edebilir.
     */
    @PutMapping("/reservations/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    /**
     * Kullanıcının tüm rezervasyonlarını getirir.
     */
    @GetMapping("/reservations/user/{userId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }

    /**
     * Tüm rezervasyonları listeler.
     * Admin panelinde rezervasyon yönetimi için.
     */
    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    /**
     * Belirli kitabın aktif rezervasyonlarını listeler.
     * Admin kitap teslim ederken hangi kullanıcı bekliyor görmek için.
     */
    @GetMapping("/admin/reservations/book/{bookId}")
    public ResponseEntity<List<ReservationResponse>> getActiveReservationsByBook(
            @PathVariable Long bookId) {
        return ResponseEntity.ok(reservationService.getActiveReservationsByBook(bookId));
    }

    /**
     * Süresi dolmuş aktif rezervasyonları EXPIRED olarak işaretler.
     * Manuel tetikleme veya zamanlanmış görev ile çağrılabilir.
     */
    @PutMapping("/admin/reservations/expire")
    public ResponseEntity<Void> expireReservations() {
        reservationService.expireReservations();
        return ResponseEntity.noContent().build();
    }
}