package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Kitapların master kaydını tutar.
 * Önemli: Bu tablo fiziksel kopyaları DEĞİL, kitap bilgisini tutar.
 * Fiziksel kopyalar BookCopy tablosunda ayrı ayrı tutulur.
 * Soft Delete uygulanmıştır:
 * - isActive = false olan kayıtlar "silinmiş" sayılır
 * - Veritabanından fiziksel olarak silinmez, veri korunur
 */

@Data
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Kitabın başlığı — boş olamaz */
    @Column(nullable = false)
    private String title;

    /**
     * Uluslararası Standart Kitap Numarası.
     * Benzersiz olmalıdır — aynı ISBN'e sahip iki farklı kitap olamaz.
     */
    @Column(unique = true)
    private String isbn;

    /**
     * Kitabın yazarı.
     * ManyToOne: Birçok kitap aynı yazara ait olabilir.
     * nullable = true → Yazarsız kitap eklenebilir.
     */
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    /**
     * Kitabın yayınevi.
     * ManyToOne: Birçok kitap aynı yayınevine ait olabilir.
     */
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    /**
     * Kitabın kategorisi.
     * ManyToOne: Birçok kitap aynı kategoriye ait olabilir.
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Kitabın bulunduğu raf.
     * ManyToOne: Bir rafta birçok kitap olabilir.
     */
    @ManyToOne
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;

    /** Kitap açıklaması özeti */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Arama için anahtar kelimeler.
     */
    private String keywords;

    /** Kitabın sayfa sayısı — okuma istatistikleri için kullanılır */
    private Integer pageCount;

    /** Kapak görseli URL'i — Google Books'tan veya manuel girilir */
    private String thumbnail;

    /**
     * Bu kitabın fiziksel kopyaları.
     * OneToMany: Bir kitabın birden fazla fiziksel kopyası olabilir.
     * mappedBy = "book" → ilişkiyi BookCopy.book alanı yönetir.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookCopy> copies = new ArrayList<>();

    /**
     * Soft delete alanı.
     * true → Kitap aktif, listelenir.
     * false → Kitap "silinmiş", listelemelerde gösterilmez ama veritabanında durur.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Soft delete tarihi.
     * isActive false yapıldığında bu alan doldurulur.
     * isActive tekrar true yapıldığında null'a çekilir.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** Kaydın oluşturulma tarihi — güncellenmez */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Yeni kayıt her zaman aktif başlar
        if (isActive == null) isActive = true;
    }
}