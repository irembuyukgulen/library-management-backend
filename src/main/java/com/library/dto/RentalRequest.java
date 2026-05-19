package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Kiralama başlatma isteği için DTO.
 * Kiralama sadece admin tarafından yapılır.
 * Üye gelir, admin sisteme girer ve kiralama başlatır.
 * reservationId opsiyonel:
 * - Rezervasyondan geldiyse → reservation dolu, ilgili rezervasyon FULFILLED yapılır
 * - Direkt kiralamaysa → reservation null
 */
@Data
public class RentalRequest {

    /**
     * Kiralanacak kitabın ISBN'i.
     * Sistemde müsait olan ilk kopya otomatik seçilir.
     */
    @NotNull(message = "ISBN boş olamaz")
    private String isbn;

    /**
     * Kitabı kiralayan kullanıcının ID'si.
     * Kullanıcı yasaklı olmamalı.
     */
    @NotNull(message = "Kullanıcı ID boş olamaz")
    private Long userId;

    /**
     * Rezervasyondan kiralama yapılıyorsa rezervasyon ID'si.
     * Null olabilir — direkt kiralama için.
     */
    private Long reservationId;
}