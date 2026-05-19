package com.library.controller;

import com.library.dto.PenaltyRequest;
import com.library.dto.PenaltyResponse;
import com.library.service.PenaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Ceza yönetimi endpoint'lerini sunan Controller.
 * Yetkilendirme:
 * /api/admin/penalties/** → Sadece ADMIN
 * /api/penalties/**       → Giriş yapılmış herkes
 * Endpoint'ler:
 * POST /api/admin/penalties               → Ceza ekle (ADMIN)
 * GET  /api/admin/penalties               → Tüm cezalar (ADMIN)
 * GET  /api/penalties/user/{userId}       → Kullanıcının cezaları
 * GET  /api/penalties/rental/{rentalId}   → Kiralamaya ait cezalar
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    /**
     * Yeni ceza ekler.
     * Gecikme (LATE) veya hasar (DAMAGE) cezası eklenebilir.
     */
    @PostMapping("/admin/penalties")
    public ResponseEntity<PenaltyResponse> addPenalty(
            @Valid @RequestBody PenaltyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(penaltyService.addPenalty(request));
    }

    /**
     * Tüm cezaları listeler.
     * Admin panelinde ceza yönetimi için.
     */
    @GetMapping("/admin/penalties")
    public ResponseEntity<List<PenaltyResponse>> getAllPenalties() {
        return ResponseEntity.ok(penaltyService.getAllPenalties());
    }

    /**
     * Kullanıcının tüm cezalarını getirir.
     * Hem admin hem üye (kendi cezaları) kullanabilir.
     */
    @GetMapping("/penalties/user/{userId}")
    public ResponseEntity<List<PenaltyResponse>> getPenaltiesByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(penaltyService.getPenaltiesByUser(userId));
    }

    /**
     * Belirli kiralamaya ait cezaları getirir.
     * Kiralama detay sayfasında ceza geçmişi için.
     */
    @GetMapping("/penalties/rental/{rentalId}")
    public ResponseEntity<List<PenaltyResponse>> getPenaltiesByRental(
            @PathVariable Long rentalId) {
        return ResponseEntity.ok(penaltyService.getPenaltiesByRental(rentalId));
    }
}