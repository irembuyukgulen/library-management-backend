package com.library.controller;

import com.library.dto.InventoryReportItem;
import com.library.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Raporlama endpoint'lerini sunan Controller.
 * Tüm endpoint'ler ADMIN'e özel.
 * Endpoint'ler:
 * GET /api/admin/reports/inventory → Envanter raporu
 * GET /api/admin/statistics        → İstatistik paneli
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Envanter raporunu getirir.
     * Rapor yapısı:
     * - SUMMARY satırları → kütüphanedeki müsait kopyalar (grupla topla)
     * - RENTED satırları  → kiradaki her kopya (tek tek, iade tarihiyle)
     */
    @GetMapping("/reports/inventory")
    public ResponseEntity<List<InventoryReportItem>> getInventoryReport() {
        return ResponseEntity.ok(reportService.getInventoryReport());
    }
}