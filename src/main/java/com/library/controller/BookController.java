package com.library.controller;

import com.library.dto.BookFilterRequest;
import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.entity.BookCopy;
import com.library.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Kitap yönetimi endpoint'lerini sunan Controller.
 * Yetkilendirme:
 * GET  /api/books/**       → Giriş yapılmış herkes
 * POST /api/books/filter   → Giriş yapılmış herkes
 * /**  /api/admin/books/** → Sadece ADMIN
 * Endpoint'ler:
 * GET    /api/books                    → Tüm aktif kitaplar
 * GET    /api/books/{id}               → Kitap detayı
 * GET    /api/books/search             → Basit arama
 * GET    /api/books/fuzzy-search       → Fuzzy arama
 * POST   /api/books/filter             → Dinamik filtreleme
 * GET    /api/books/random             → Rastgele kitap
 * GET    /api/books/random/smart/{uid} → Akıllı öneri
 * GET    /api/books/{id}/copies        → Kitabın kopyaları
 * POST   /api/admin/books              → Kitap ekle (ADMIN)
 * PUT    /api/admin/books/{id}         → Kitap güncelle (ADMIN)
 * DELETE /api/admin/books/{id}         → Kitap sil (ADMIN, soft delete)
 * DELETE /api/admin/books/bulk-delete  → Toplu sil (ADMIN)
 * PUT    /api/admin/books/{id}/restore → Geri yükle (ADMIN)
 * GET    /api/admin/books/deleted      → Silinmiş kitaplar (ADMIN)
 * POST   /api/admin/books/{id}/copies  → Kopya ekle (ADMIN)
 * DELETE /api/admin/copies/{id}        → Kopya sil (ADMIN)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final FuzzySearchService fuzzySearchService;
    private final BookFilterService bookFilterService;

    /**
     * Tüm kitapları listeler.
     */
    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * ID'ye göre kitap detayını getirir.
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * Başlığa göre basit arama yapar.
     */
    @GetMapping("/books/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String title) {
        // Basit arama → tüm aktif kitaplar arasında başlık araması
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.ok(bookService.getAllBooks());
        }
        // Filter service üzerinden filtrele
        BookFilterRequest filter = new BookFilterRequest();
        filter.setTitle(title);
        return ResponseEntity.ok(bookFilterService.filterBooks(filter));
    }

    /**
     * Yazım hatalarına toleranslı fuzzy arama yapar.
     */
    @GetMapping("/books/fuzzy-search")
    public ResponseEntity<List<BookResponse>> fuzzySearch(
            @RequestParam String query) {
        return ResponseEntity.ok(fuzzySearchService.fuzzySearchBooks(query));
    }

    /**
     * Dinamik filtreleme yapar.
     * Tüm parametreler opsiyonel — null gelirse o filtre uygulanmaz.
     */
    @PostMapping("/books/filter")
    public ResponseEntity<List<BookResponse>> filterBooks(
            @RequestBody BookFilterRequest filter) {
        return ResponseEntity.ok(bookFilterService.filterBooks(filter));
    }

    /**
     * Kitabın tüm fiziksel kopyalarını listeler.
     */
    @GetMapping("/books/{id}/copies")
    public ResponseEntity<List<BookCopy>> getCopies(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getCopiesByBook(id));
    }

    /**
     * Silinmiş (soft delete) kitapları listeler.
     */
    @GetMapping("/admin/books/deleted")
    public ResponseEntity<List<BookResponse>> getDeletedBooks() {
        return ResponseEntity.ok(bookService.getDeletedBooks());
    }

    /**
     * Yeni kitap ekler.
     */
    @PostMapping("/admin/books")
    public ResponseEntity<BookResponse> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.saveBook(request, authentication.getName()));
    }

    /**
     * Mevcut kitabı günceller.
     */
    @PutMapping("/admin/books/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(bookService.updateBook(id, request, authentication.getName()));
    }

    /**
     * Kitabı soft delete yapar.
     * Veritabanında kalır — geçmiş kiralamalar bozulmaz.
     */
    @DeleteMapping("/admin/books/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id,
            Authentication authentication) {
        bookService.deleteBook(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Birden fazla kitabı aynı anda soft delete yapar.
     */
    @DeleteMapping("/admin/books/bulk-delete")
    public ResponseEntity<Void> bulkDeleteBooks(
            @Valid @RequestBody com.library.dto.BulkDeleteRequest request,
            Authentication authentication) {
        bookService.bulkDeleteBooks(request.getIds(), authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Soft delete yapılmış kitabı geri yükler.
     */
    @PutMapping("/admin/books/{id}/restore")
    public ResponseEntity<BookResponse> restoreBook(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(bookService.restoreBook(id, authentication.getName()));
    }

    /**
     * Kitaba yeni fiziksel kopya ekler.
     * Kitaba belirtilen sayıda kopya ekler.
     * Kopya kodları otomatik oluşturulur.
     */
    @PostMapping("/admin/books/{id}/copies")
    public ResponseEntity<List<BookCopy>> addCopies(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        int count = body.getOrDefault("count", 1);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.addCopies(id, count));
    }

    /**
     * Fiziksel kopyayı siler.
     */
    @DeleteMapping("/admin/copies/{id}")
    public ResponseEntity<Void> deleteCopy(@PathVariable Long id) {
        bookService.deleteCopy(id);
        return ResponseEntity.noContent().build();
    }
}