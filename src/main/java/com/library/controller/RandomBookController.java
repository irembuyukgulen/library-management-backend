package com.library.controller;

import com.library.dto.BookResponse;
import com.library.service.RandomBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rastgele kitap öneri endpoint'lerini sunan Controller.
 * Yetkilendirme:
 * Giriş yapılmış herkes kullanabilir.
 * Endpoint'ler:
 * GET /api/books/random              → Tamamen rastgele kitap
 * GET /api/books/random/smart/{userId} → Kullanıcı geçmişine göre akıllı öneri
 * Kullanım senaryoları:
 * - Üye panelinde "Bugün ne okusam?" butonu
 * - Kararsız kullanıcılar için sürpriz öneri
 * - Akıllı mod: favori kategoriden daha önce okunmamış kitap önerir
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class RandomBookController {

    private final RandomBookService randomBookService;

    /**
     * Tüm aktif kitaplar arasından tamamen rastgele bir kitap seçer.
     * Giriş yapmamış kullanıcılar veya geçmişi olmayan yeni üyeler için.
     */
    @GetMapping("/random")
    public ResponseEntity<BookResponse> getRandomBook() {
        return ResponseEntity.ok(randomBookService.getRandomBook());
    }

    /**
     * Kullanıcının okuma geçmişine göre akıllı kitap önerisi yapar.
     * Algoritma:
     * 1. Daha önce okunmamış kitapları bul
     * 2. Favori kategoriden okunmamış kitap ara
     * 3. Yoksa tüm okunmamışlardan rastgele seç
     * 4. Hepsini okumuşsa rastgele ver
     */
    @GetMapping("/random/smart/{userId}")
    public ResponseEntity<BookResponse> getSmartRandomBook(@PathVariable Long userId) {
        return ResponseEntity.ok(randomBookService.getSmartRandomBook(userId));
    }
}