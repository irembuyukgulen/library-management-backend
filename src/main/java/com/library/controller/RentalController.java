package com.library.controller;

import com.library.dto.RentalRequest;
import com.library.dto.RentalResponse;
import com.library.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kiralama işlemlerini yöneten Controller.
 * Yetkilendirme:
 * /api/admin/rentals/** → Sadece ADMIN
 * /api/rentals/**       → Giriş yapılmış herkes
 * Endpoint'ler:
 * POST /api/admin/rentals              → Kiralama başlat (ADMIN)
 * PUT  /api/admin/rentals/{id}/return  → İade et (ADMIN)
 * GET  /api/admin/rentals/active       → Aktif kiralamalar (ADMIN)
 * GET  /api/admin/rentals/overdue      → Gecikmiş kiralamalar (ADMIN)
 * GET  /api/rentals/user/{userId}      → Kullanıcının kiralamaları
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    /**
     * Yeni kiralama başlatır.
     * Admin kütüphanede kitap teslim ederken bu endpoint kullanılır.
     */
    @PostMapping("/admin/rentals")
    public ResponseEntity<RentalResponse> createRental(
            @Valid @RequestBody RentalRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalService.createRental(request, authentication.getName()));
    }

    /**
     * Kitabı iade eder.
     * Admin kütüphanede kitap teslim alırken bu endpoint kullanılır.
     */
    @PutMapping("/admin/rentals/{id}/return")
    public ResponseEntity<RentalResponse> returnRental(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(rentalService.returnRental(id, authentication.getName()));
    }

    /**
     * Tüm aktif kiralamaları listeler.
     * Admin panelinde kiralama yönetimi için kullanılır.
     * İade tarihine göre sıralı — önce acil olanlar.
     */
    @GetMapping("/admin/rentals/active")
    public ResponseEntity<List<RentalResponse>> getActiveRentals() {
        return ResponseEntity.ok(rentalService.getActiveRentals());
    }

    /**
     * Gecikmiş kiralamaları listeler ve OVERDUE olarak işaretler.
     * Admin panelinde takip ve ceza işlemleri için.
     */
    @GetMapping("/admin/rentals/overdue")
    public ResponseEntity<List<RentalResponse>> getOverdueRentals() {
        return ResponseEntity.ok(rentalService.getOverdueRentals());
    }

    /**
     * Belirli bir kullanıcının tüm kiralamalarını getirir.
     * Hem admin (herhangi kullanıcı) hem üye (kendi kiralamaları) kullanabilir.
     */
    @GetMapping("/rentals/user/{userId}")
    public ResponseEntity<List<RentalResponse>> getRentalsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(rentalService.getRentalsByUser(userId));
    }
}