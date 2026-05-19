package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Kitap rezervasyonlarını temsil eder.
 * Rezervasyon mantığı:
 * - Kullanıcı şu an kirada olan bir kitabı "ayırtabilir".
 * - Kitap iade edilince rezervasyonu olan kullanıcıya bildirim gönderilir.
 * - Rezervasyon belirli bir süre sonra otomatik iptal edilir (EXPIRED).
 * Kiralama başlarken kopya atanır.
 */

@Data
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Rezerve edilen kitap.
     */
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /** Rezervasyonu yapan kullanıcı */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Rezervasyon yapılma tarihi */
    @Column(nullable = false)
    private LocalDate reservationDate;

    /**
     * Rezervasyonun son geçerlilik tarihi.
     */
    private LocalDate expiryDate;

    /**
     * Rezervasyon durumu.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    /** Kaydın oluşturulma tarihi */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** Rezervasyon durum enum'u */
    public enum ReservationStatus {
        ACTIVE,     // Aktif
        FULFILLED,  // Tamamlandı (kiralama başladı)
        CANCELLED,  // İptal edildi
        EXPIRED     // Süresi doldu
    }
}