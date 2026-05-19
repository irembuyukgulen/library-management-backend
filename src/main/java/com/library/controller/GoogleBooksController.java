package com.library.controller;

import com.library.dto.BookResponse;
import com.library.dto.GoogleBookResponse;
import com.library.service.BookService;
import com.library.service.GoogleBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Google Books API entegrasyon endpoint'lerini sunan Controller.
 * Tüm endpoint'ler ADMIN'e özel — kitap ekleme işlemi.
 * Endpoint'ler:
 * GET  /api/admin/google-books/{isbn}       → ISBN ile kitap bilgisi çek (önizleme)
 * POST /api/admin/google-books/{isbn}/save  → ISBN ile çek ve direkt kaydet
 * Kullanım senaryosu:
 * 1. Admin ISBN girer
 * 2. GET isteği → Google Books'tan bilgi çekilir, form doldurulur
 * 3. Admin eksik alanları tamamlar (raf, kütüphane vb.)
 * 4. POST /save → sistem kitabı kaydeder
 * Alternatif: GET sonrasında admin BookController'daki
 * POST /api/admin/books endpoint'ini kullanabilir.
 */
@RestController
@RequestMapping("/api/admin/google-books")
@RequiredArgsConstructor
public class GoogleBooksController {

    private final GoogleBooksService googleBooksService;
    private final BookService bookService;

    /**
     * ISBN ile Google Books'tan kitap bilgisini çeker.
     * Form ön doldurma (preview) için kullanılır.
     * Veritabanına kaydetmez.
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<GoogleBookResponse> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(googleBooksService.getBookByIsbn(isbn));
    }

    /**
     * ISBN ile Google Books'tan kitap bilgisini çeker ve direkt kaydeder.
     * "Hızlı ekle" özelliği — admin sadece ISBN girer.
     * Eksik bilgiler (raf, kütüphane vb.) sonradan güncellenebilir.
     */
    @PostMapping("/{isbn}/save")
    public ResponseEntity<BookResponse> saveBookFromGoogle(
            @PathVariable String isbn,
            Authentication authentication) {

        // Google Books'tan BookRequest'e dönüştür
        var request = googleBooksService.getBookRequestByIsbn(isbn);

        // Kaydet (FindOrCreateService otomatik yazar/yayınevi/kategori oluşturur)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.saveBook(request, authentication.getName()));
    }
}