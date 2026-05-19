package com.library.dto;

import lombok.Data;

/**
 * Dinamik kitap filtreleme isteği için DTO.
 * Tüm alanlar opsiyonel — null gelirse o filtre uygulanmaz.
 * Bu sayede frontend istediği kombinasyonu gönderebilir.
 * Örnek kullanımlar:
 * - Sadece müsait kitaplar: { "status": "AVAILABLE" }
 * - Belirli kategoride: { "categoryId": 1 }
 * - Kombinasyon: { "categoryId": 1, "status": "AVAILABLE", "sortBy": "title" }
 * Frontend'den POST isteğiyle gönderilir:
 * POST /api/books/filter
 */
@Data
public class BookFilterRequest {

    /** Başlığa göre filtrele — kısmi eşleşme */
    private String title;

    /** Anahtar kelimeye göre filtrele */
    private String keyword;

    /** Yazar ID'sine göre filtrele */
    private Long authorId;

    /** Yayınevi ID'sine göre filtrele */
    private Long publisherId;

    /** Kategori ID'sine göre filtrele */
    private Long categoryId;

    /** Kütüphane ID'sine göre filtrele */
    private Long libraryId;

    /** Raf ID'sine göre filtrele */
    private Long shelfId;

    /**
     * Kopya durumuna göre filtrele.
     * "AVAILABLE" → sadece müsait kitaplar
     * "RENTED"    → kiradaki kitaplar
     */
    private String status;

    /**
     * Sıralama alanı.
     * "title"     → başlığa göre
     * "author"    → yazara göre
     * "createdAt" → oluşturma tarihine göre
     */
    private String sortBy;

    /**
     * Sıralama yönü.
     * "asc"  → artan
     * "desc" → azalan
     */
    private String sortDir;

    /**
     * Sayfa numarası (0'dan başlar).
     * Null → sayfalama uygulanmaz, tümü döner
     */
    private Integer page;

    /**
     * Sayfa başına kayıt sayısı.
     * Null → sayfalama uygulanmaz
     */
    private Integer size;
}