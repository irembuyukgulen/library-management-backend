package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kitap kiralamalarını temsil eder.
 * Kiralama akışı:
 * 1. Admin kopyayı seçer ve kiralama başlatır.
 * 2. BookCopy durumu RENTED olur.
 * 3. Kullanıcı dueDate'e kadar kitabı iade etmeli.
 * 4. İade edilmezse gecikme cezası (Penalty) oluşur.
 * 5. İade edilince BookCopy durumu AVAILABLE olur.
 */

@Data
@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Kiralanan fiziksel kopya.
     */
    @ManyToOne
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    /** Kiralayan kullanıcı */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Rezervasyondan gelen kiralama için bağlantı.
     */
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    /** Kiralamanın başladığı tarih */
    @Column(nullable = false)
    private LocalDate rentalDate;

    /** İade edilmesi gereken son tarih */
    @Column(nullable = false)
    private LocalDate dueDate;

    /**
     * Gerçek iade tarihi.
     */
    private LocalDate returnDate;

    /**
     * Günlük kiralama ücreti.
     */
    @Column(nullable = false)
    private BigDecimal dailyFee;

    /**
     * Kiralama durumu.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status = RentalStatus.ACTIVE;

    /**
     * Bu kiralamaya ait cezalar.
     * Gecikme cezası veya hasar cezası olabilir.
     */
    @OneToMany(mappedBy = "rental")
    private List<Penalty> penalties;

    /** Kaydın oluşturulma tarihi */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Kiralama başlangıç tarihi otomatik set edilir
        rentalDate = LocalDate.now();
    }

    /** Kiralama durum enum'u */
    public enum RentalStatus {
        ACTIVE,   // Aktif (kirada)
        RETURNED, // İade edildi
        OVERDUE   // Gecikmiş
    }
}