package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Kitapların fiziksel kopyalarını temsil eder.
 */

@Data
@Entity
@Table(name = "book_copies")
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Bu kopyanın ait olduğu kitap.
     * nullable = false → Her kopya bir kitaba ait olmak zorunda.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Kopyanın benzersiz kodu.
     * Barkod veya etiket numarası olarak kullanılabilir.
     */
    @Column(unique = true)
    private String copyCode;

    /**
     * Kopyanın anlık durumu.
     * AVAILABLE → Kütüphanede, kiralanabilir.
     * RENTED    → Şu an kirada, müsait değil.
     * RESERVED  → Rezerve edilmiş, bekliyor.
     * DAMAGED   → Hasarlı, kiralamaya kapalı.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status = CopyStatus.AVAILABLE;

    /** Kaydın oluşturulma tarihi */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** Kopya durum enum'u */
    public enum CopyStatus {
        AVAILABLE,  // Müsait
        RENTED,     // Kirada
        RESERVED,   // Rezerveli
        DAMAGED     // Hasarlı
    }
}