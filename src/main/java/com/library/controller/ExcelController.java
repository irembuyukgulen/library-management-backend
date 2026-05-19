package com.library.controller;

import com.library.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Excel import/export endpoint'lerini sunan Controller.
 * Tüm endpoint'ler ADMIN'e özel.
 * Endpoint'ler:
 * GET  /api/admin/books/export → Kitapları Excel'e aktar
 * POST /api/admin/books/import → Excel'den kitapları içe aktar
 * Export formatı:
 * ID | Başlık | ISBN | Yazar | Yayınevi | Kategori | Raf |
 * Açıklama | Anahtar Kelimeler | Toplam Kopya | Müsait Kopya | Durum
 * Import formatı:
 * Başlık | ISBN | Yazar | Yayınevi | Kategori | Açıklama | Anahtar Kelimeler
 * (1. satır başlık, 2. satırdan itibaren veri)
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    /**
     * Tüm aktif kitapları Excel dosyası olarak indirir.
     */
    @GetMapping("/books/export")
    public ResponseEntity<byte[]> exportBooks() throws IOException {
        byte[] data = excelService.exportBooksToExcel();

        String filename = "kitaplar_" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    /**
     * Excel dosyasından kitapları toplu olarak içe aktarır.
     */
    @PostMapping("/books/import")
    public ResponseEntity<?> importBooks(@RequestParam("file") MultipartFile file)
            throws IOException {

        // Dosya tipi kontrolü
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".xlsx")) {
            return ResponseEntity.badRequest()
                    .body(List.of("Hata: Sadece .xlsx formatında dosya kabul edilmektedir."));
        }

        // Dosya boş mu?
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(List.of("Hata: Dosya boş."));
        }

        List<String> results = excelService.importBooksFromExcel(file);
        return ResponseEntity.ok(results);
    }
}