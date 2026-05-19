package com.library.dto;

import lombok.Data;

/**
 * Google Books API'den dönen kitap bilgisi için DTO.
 * Google Books API'ye ISBN ile istek atılır,
 * bu DTO doldurulur ve frontend'e döndürülür.
 * Frontend bu bilgileri kitap ekleme formuna otomatik doldurur.
 * Admin eksik alanları tamamlayıp kaydeder.
 * Thumbnail öncelik sırası:
 * extraLarge > large > medium > thumbnail
 * En yüksek kaliteli resim seçilir.
 */
@Data
public class GoogleBookResponse {

    /** Kitabın başlığı */
    private String title;

    /**
     * Yazarlar — virgülle ayrılmış.
     * Google Books liste döndürür, biz birleştiririz.
     */
    private String authors;

    /** Yayınevinin adı */
    private String publisher;

    /** Yayın tarihi — "2003" veya "2003-07-01" formatında */
    private String publishedDate;

    /** Kitap açıklaması */
    private String description;

    /** ISBN numarası — arama için kullanılan */
    private String isbn;

    /**
     * Kapak resmi URL'i.
     * En yüksek kaliteli mevcut resim seçilir.
     * HTTP → HTTPS dönüşümü yapılır.
     */
    private String thumbnail;

    /**
     * Sayfa sayısı.
     * Okuma zaman tüneli hesaplaması için kullanılır.
     */
    private Integer pageCount;

    /**
     * Kategori.
     * Google Books listesinin ilk elemanı alınır.
     */
    private String categories;

    /**
     * Kitabın dili.
     * Örnek: "tr", "en"
     */
    private String language;
}