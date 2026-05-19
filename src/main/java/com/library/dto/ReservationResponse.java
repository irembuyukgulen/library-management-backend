package com.library.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Rezervasyon bilgisi döndürmek için DTO.
 * Rezervasyon durumları:
 * ACTIVE    → Aktif, kitap bekleniyor
 * FULFILLED → Kitap teslim edildi, kiralama başladı
 * CANCELLED → İptal edildi (kullanıcı veya admin tarafından)
 * EXPIRED   → Süre doldu (expiryDate geçti)
 */
@Data
public class ReservationResponse {

    /** Rezervasyon ID'si */
    private Long id;

    /** Rezerve edilen kitabın başlığı */
    private String bookTitle;

    /** Kitabın ID'si */
    private Long bookId;

    /** Rezervasyonu yapan kullanıcının adı soyadı */
    private String userName;

    /** Kullanıcının email adresi */
    private String userEmail;

    /** Kullanıcının ID'si */
    private Long userId;

    /** Rezervasyon tarihi */
    private LocalDate reservationDate;

    /**
     * Rezervasyonun geçerlilik bitiş tarihi.
     * Bu tarihe kadar kitap teslim edilmezse EXPIRED olur.
     */
    private LocalDate expiryDate;

    /** Rezervasyon durumu: ACTIVE, FULFILLED, CANCELLED, EXPIRED */
    private String status;

    /** Kayıt tarihi */
    private LocalDateTime createdAt;
}