package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Şifre güncelleme isteği için kullanılan DTO.
 * Kullanıcı profil sayfasından şifre değiştirmek istediğinde
 * frontend bu nesneyi POST body olarak gönderir.
 * Doğrulama kuralları:
 * - currentPassword boş olamaz (mevcut şifre doğrulama için)
 * - newPassword boş olamaz ve en az 6 karakter olmalıdır
 */
@Data
public class UpdatePasswordRequest {

    /**
     * Kullanıcının mevcut şifresi.
     * Yeni şifre kaydetmeden önce kimlik doğrulaması için kullanılır.
     */
    @NotBlank(message = "Mevcut şifre boş olamaz")
    private String currentPassword;

    /**
     * Kullanıcının belirlemek istediği yeni şifre.
     * Minimum 6 karakter uzunluğunda olmalıdır.
     */
    @NotBlank(message = "Yeni şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String newPassword;
}