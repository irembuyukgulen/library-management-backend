package com.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Profil güncelleme isteği için kullanılan DTO.
 * Kullanıcı profil sayfasından ad, soyad veya email
 * bilgisini güncellemek istediğinde frontend bu nesneyi
 * PUT body olarak gönderir.
 * Doğrulama kuralları:
 * - name ve surname boş olamaz, 2-100 karakter arasında olmalıdır
 * - email boş olamaz ve geçerli formatta olmalıdır
 */
@Data
public class UpdateProfileRequest {

    /**
     * Kullanıcının güncellemek istediği adı.
     * En az 2, en fazla 100 karakter olmalıdır.
     */
    @NotBlank(message = "Ad boş olamaz")
    @Size(min = 2, max = 100)
    private String name;

    /**
     * Kullanıcının güncellemek istediği soyadı.
     * En az 2, en fazla 100 karakter olmalıdır.
     */
    @NotBlank(message = "Soyad boş olamaz")
    @Size(min = 2, max = 100)
    private String surname;

    /**
     * Kullanıcının güncellemek istediği email adresi.
     * Geçerli bir email formatında olmalıdır.
     */
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email giriniz")
    private String email;
}