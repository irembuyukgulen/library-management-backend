package com.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Kullanıcı giriş isteği için DTO.
 * Validation hatası olursa GlobalExceptionHandler 400 döner.
 */
@Data
public class AuthRequest {

    /**
     * Kullanıcının email adresi.
     * @NotBlank → null, boş string veya sadece boşluk olamaz
     * @Email → geçerli email formatı olmalı
     */
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    /**
     * Kullanıcının şifresi.
     * @Size → en az 6 karakter olmalı
     */
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;
}