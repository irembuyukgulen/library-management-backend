package com.library.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Kitap bilgisi döndürmek için DTO.
 * Entity'deki ilişkili nesneler (Author, Publisher vb.)
 * burada sadece isim olarak döndürülür — ID değil.
 * Frontend için daha kullanışlı.
 * Kopya sayıları BookCopyRepository'den hesaplanır.
 */
@Data
public class BookResponse {

    /** Kitabın ID'si */
    private Long id;

    /** Kitabın başlığı */
    private String title;

    /** ISBN numarası */
    private String isbn;

    /** Yazarın adı */
    private String authorName;

    /** Yazarın ID'si — düzenleme formunda seçim için */
    private Long authorId;

    /** Yayınevinin adı */
    private String publisherName;

    /** Yayınevinin ID'si */
    private Long publisherId;

    /** Kategorinin adı */
    private String categoryName;

    /** Kategorinin ID'si */
    private Long categoryId;

    /** Rafın numarası */
    private String shelfNumber;

    /** Rafın ID'si */
    private Long shelfId;

    /** Kütüphanenin adı */
    private String libraryName;

    /** Kütüphanenin ID'si */
    private Long libraryId;

    /** Kitap açıklaması */
    private String description;

    /** Arama için anahtar kelimeler */
    private String keywords;

    /** Kitabın toplam sayfa sayısı */
    private Integer pageCount;

    /** Kapak resmi URL'i */
    private String thumbnail;

    /** Toplam fiziksel kopya sayısı */
    private int totalCopies;

    /** Şu an müsait (kiralanabilir) kopya sayısı */
    private int availableCopies;

    /**
     * Kitabın aktif olup olmadığı.
     * false → soft delete yapılmış, listede görünmez
     */
    private Boolean isActive;

    /** Oluşturulma tarihi */
    private LocalDateTime createdAt;
}