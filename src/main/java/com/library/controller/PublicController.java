package com.library.controller;

import com.library.entity.SystemSettings;
import com.library.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kimlik doğrulaması gerektirmeyen (public) endpoint'leri sunan Controller.
 * SecurityConfig'de /api/public/** path'i permitAll olarak tanımlanmıştır.
 * Bu sayede giriş yapmamış kullanıcılar da bu endpoint'lere erişebilir.
 * Kullanım amacı:
 * Üye rezervasyon ekranında günlük kiralama ücreti ve iade süresi gibi
 * sistem ayarlarının JWT token olmadan çekilebilmesi için oluşturulmuştur.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final SystemSettingsService systemSettingsService;

    /**
     * Sistem ayarlarını herkese açık olarak döndürür.
     * Kimlik doğrulaması gerekmez — üye panelinde rezervasyon
     * modalında ücret ve süre bilgisi göstermek için kullanılır.
     * Endpoint: GET /api/public/settings
     */
    @GetMapping("/settings")
    public ResponseEntity<List<SystemSettings>> getPublicSettings() {
        return ResponseEntity.ok(systemSettingsService.getAllSettings());
    }
}