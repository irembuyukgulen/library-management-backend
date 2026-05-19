package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Rezervasyon oluşturma isteği için DTO.
 * Rezervasyon kitap bazında yapılır — kopya bazında değil.
 * Kullanıcı "Harry Potter istiyorum" der, hangi kopyanın
 * geleceği kiralama sırasında belirlenir.
 * Hem üye hem admin rezervasyon oluşturabilir.
 */
@Data
public class ReservationRequest {

    /**
     * Rezerve edilecek kitabın ID'si.
     * Kitabın aktif (isActive=true) olması gerekir.
     */
    @NotNull(message = "Kitap ID boş olamaz")
    private Long bookId;

    /**
     * Rezervasyonu yapan kullanıcının ID'si.
     * Kullanıcı yasaklı olmamalı.
     * Aynı kitap için aktif rezervasyonu olmamalı.
     */
    @NotNull(message = "Kullanıcı ID boş olamaz")
    private Long userId;
}