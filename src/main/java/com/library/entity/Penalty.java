package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cezaları temsil eder.
 * Sadece ceza kaydı tutulur, ödeme takibi yapılmaz.
 * Yönetici ceza durumuna göre kullanıcıyı yasaklayabilir (User.isBanned = true).
 */

@Data
@Entity
@Table(name = "penalties")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Cezanın bağlı olduğu kiralama */
    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    /** Ceza kesilen kullanıcı */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Ceza türü.
     * LATE   → Geç iade cezası
     * DAMAGE → Hasar cezası
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyType type;

    /**
     * Ceza miktarı
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /** Ceza açıklaması gerekçesi */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Cezanın oluşturulma tarihi */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** Ceza türü enum'u */
    public enum PenaltyType {
        LATE,   // Geç iade
        DAMAGE  // Hasar
    }
}