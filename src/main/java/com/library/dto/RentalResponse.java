package com.library.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Kiralama bilgisi döndürmek için DTO.
 * Gecikme hesaplaması:
 * - returnDate varsa → returnDate ile dueDate farkı
 * - returnDate yoksa → bugün ile dueDate farkı
 * - daysOverdue > 0 → gecikmiş demektir
 * Toplam ücret hesaplaması:
 * - Normal ücret: rentalDays × dailyFee
 * - Gecikme ücreti: daysOverdue × dailyLateFee
 * - Toplam: normalFee + lateFee
 */
@Data
public class RentalResponse {

    /** Kiralama ID'si */
    private Long id;

    /** Kitabın başlığı */
    private String bookTitle;

    /** Kopya kodu */
    private String copyCode;

    /** Kitabı kiralayan kullanıcının adı soyadı */
    private String userName;

    /** Kullanıcının email adresi */
    private String userEmail;

    /** Kullanıcının ID'si */
    private Long userId;

    /** Kiralama tarihi */
    private LocalDate rentalDate;

    /** İade edilmesi gereken son tarih */
    private LocalDate dueDate;

    /**
     * Gerçek iade tarihi.
     * Null → henüz iade edilmemiş
     */
    private LocalDate returnDate;

    /** Günlük kiralama ücreti */
    private BigDecimal dailyFee;

    /** Kiralama durumu: ACTIVE, RETURNED, OVERDUE */
    private String status;

    /**
     * Gecikme gün sayısı.
     * 0 → gecikmemiş
     * > 0 → bu kadar gün gecikmiş
     */
    private Long daysOverdue;

    /**
     * Toplam ödenecek ücret.
     * Kiralama ücreti + gecikme cezası (varsa)
     */
    private BigDecimal totalFee;

    /** Kayıt tarihi */
    private LocalDateTime createdAt;
}