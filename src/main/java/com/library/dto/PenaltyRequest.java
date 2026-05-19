package com.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Ceza ekleme isteği için DTO.
 * Ceza türleri:
 * LATE   → Gecikme cezası — kitap zamanında iade edilmedi
 * DAMAGE → Hasar cezası — kitap hasarlı iade edildi
 * Sadece admin ceza ekleyebilir.
 */
@Data
public class PenaltyRequest {

    /**
     * Cezanın ait olduğu kiralama ID'si.
     */
    @NotNull(message = "Kiralama ID boş olamaz")
    private Long rentalId;

    /**
     * Ceza uygulanan kullanıcının ID'si.
     */
    @NotNull(message = "Kullanıcı ID boş olamaz")
    private Long userId;

    /**
     * Ceza türü: "LATE" veya "DAMAGE"
     */
    @NotNull(message = "Ceza türü boş olamaz")
    private String type;

    /**
     * Ceza miktarı (TL).
     * Pozitif bir değer olmalı.
     */
    @NotNull(message = "Ceza miktarı boş olamaz")
    @Positive(message = "Ceza miktarı pozitif olmalıdır")
    private Double amount;

    /**
     * Ceza açıklaması — opsiyonel.
     */
    private String description;
}