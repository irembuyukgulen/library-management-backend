package com.library.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ceza bilgisi döndürmek için DTO.
 */
@Data
public class PenaltyResponse {

    /** Ceza ID'si */
    private Long id;

    /** Cezanın ait olduğu kiralama ID'si */
    private Long rentalId;

    /** Kitabın başlığı */
    private String bookTitle;

    /** Ceza uygulanan kullanıcının adı soyadı */
    private String userName;

    /** Kullanıcının email adresi */
    private String userEmail;

    /** Ceza türü: LATE veya DAMAGE */
    private String type;

    /** Ceza miktarı (TL) */
    private BigDecimal amount;

    /** Ceza açıklaması */
    private String description;

    /** Cezanın oluşturulma tarihi */
    private LocalDateTime createdAt;
}