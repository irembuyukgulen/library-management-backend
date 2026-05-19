package com.library.controller;

import com.library.dto.StatisticsResponse;
import com.library.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * İstatistik paneli endpoint'lerini sunan Controller.
 * Tüm endpoint'ler ADMIN'e özel — SecurityConfig'de tanımlı.
 * Endpoint'ler:
 * GET /api/admin/statistics → Dashboard istatistik verileri
 * Dönen veri:
 * - Kitap/kopya sayıları (toplam, müsait, kirada)
 * - Üye sayısı
 * - Aktif/gecikmiş kiralama sayıları
 * - Aktif/toplam rezervasyon sayıları
 * - En çok kiralanan kitaplar (top 5)
 * - En aktif üyeler (top 5)
 * Frontend bu veriyle dashboard kartları ve chart'ları oluşturur.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Admin dashboard için tüm istatistik verilerini getirir.
     * Tek endpoint'ten tüm veri — frontend tek istekle dashboard'u doldurur.
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}