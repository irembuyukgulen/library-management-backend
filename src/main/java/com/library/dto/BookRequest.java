package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Kitap ekleme ve güncelleme isteği için DTO.
 * "Find or Create" mantığı:
 * ID yerine isim gönderilir. Service katmanı:
 * 1. İsimle veritabanında arar
 * 2. Bulursa mevcut kaydı kullanır
 * 3. Bulamazsa yeni kayıt oluşturur
 * Bu sayede frontend veya Postman'de ID bilmek gerekmez.
 * Aynı yazar/yayınevi/kategori iki kez eklenmez.
 * Google Books'tan otomatik doldurma:
 * GoogleBooksService bu DTO'ya dönüştürür,
 * admin eksik alanları tamamlayıp kaydeder.
 */
@Data
public class BookRequest {

    /**
     * Kitabın başlığı — zorunlu alan.
     */
    @NotBlank(message = "Kitap adı boş olamaz")
    @Size(max = 255, message = "Kitap adı en fazla 255 karakter olabilir")
    private String title;

    /**
     * Kitap eklenirken kaç kopya oluşturulsun?
     * Null veya 0 ise kopya oluşturulmaz, sonradan eklenebilir.
     */
    private Integer copyCount;

    /**
     * ISBN numarası — opsiyonel.
     * Google Books'tan otomatik doldurulur.
     */
    @Size(max = 20, message = "ISBN en fazla 20 karakter olabilir")
    private String isbn;

    /**
     * Yazarın adı — opsiyonel.
     * FindOrCreateService tarafından işlenir:
     * - Sistemde varsa mevcut yazar kullanılır
     * - Yoksa yeni yazar oluşturulur
     */
    private String authorName;

    /**
     * Yayınevinin adı — opsiyonel.
     * FindOrCreateService tarafından işlenir.
     */
    private String publisherName;

    /**
     * Kategorinin adı — opsiyonel.
     * FindOrCreateService tarafından işlenir.
     */
    private String categoryName;

    /**
     * Kütüphanenin adı — opsiyonel.
     * FindOrCreateService tarafından işlenir.
     * Raf eklenirken hangi kütüphaneye ait olduğunu belirtir.
     */
    private String libraryName;

    /**
     * Kütüphanenin adresi — opsiyonel.
     * Sadece yeni kütüphane oluşturulurken kullanılır.
     */
    private String libraryAddress;

    /**
     * Kütüphanenin telefon numarası — opsiyonel.
     * Sadece yeni kütüphane oluşturulurken kullanılır.
     */
    private String libraryPhone;

    /**
     * Raf numarası — opsiyonel.
     * Örnek: "A1", "B3"
     * FindOrCreateService tarafından işlenir.
     */
    private String shelfNumber;

    /**
     * Raf bölmesi — opsiyonel.
     * Örnek: "Roman Bölümü"
     * Sadece yeni raf oluşturulurken kullanılır.
     */
    private String shelfSection;

    /**
     * Kitap açıklaması — opsiyonel.
     * Google Books'tan otomatik doldurulur.
     */
    private String description;

    /**
     * Arama için anahtar kelimeler — opsiyonel.
     * Virgülle ayrılmış format: "distopya, siyaset, gelecek"
     */
    private String keywords;

    /**
     * Kitabın sayfa sayısı — opsiyonel.
     * Google Books'tan otomatik doldurulur.
     * Okuma zaman tüneli hesaplamasında kullanılır.
     */
    private Integer pageCount;

    /**
     * Kapak resmi URL'i — opsiyonel.
     * Google Books'tan otomatik doldurulur.
     * En yüksek kaliteli resim seçilir.
     */
    private String thumbnail;
}