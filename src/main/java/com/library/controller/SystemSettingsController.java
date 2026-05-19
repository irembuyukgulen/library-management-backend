package com.library.controller;

import com.library.entity.SystemSettings;
import com.library.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Sistem ayarları endpoint'lerini sunan Controller.
 * Tüm endpoint'ler ADMIN'e özel — SecurityConfig'de tanımlı.
 * Endpoint'ler:
 * GET  /api/admin/settings → Tüm ayarları getir
 * POST /api/admin/settings → Ayarları güncelle (toplu)
 * Ayarlar key-value formatında saklanır:
 * standard_loan_days → "14"
 * daily_rental_fee   → "2.50"
 * daily_late_fee     → "5.00"
 * reservation_days   → "7"
 */
@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class SystemSettingsController {

    private final SystemSettingsService systemSettingsService;

    /**
     * Tüm sistem ayarlarını getirir.
     * Admin panelinde ayarlar formu için kullanılır.
     */
    @GetMapping
    public ResponseEntity<List<SystemSettings>> getAllSettings() {
        return ResponseEntity.ok(systemSettingsService.getAllSettings());
    }

    /**
     * Sistem ayarlarını toplu günceller.
     * Admin panelinde "Kaydet" butonuna basınca tüm ayarlar güncellenir.
     */
    @PostMapping
    public ResponseEntity<Void> saveAllSettings(@RequestBody Map<String, String> settings) {
        systemSettingsService.saveAllSettings(settings);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/settings")
    public ResponseEntity<List<SystemSettings>> getPublicSettings() {
        return ResponseEntity.ok(systemSettingsService.getAllSettings());
    }
}