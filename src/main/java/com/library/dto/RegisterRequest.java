package com.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Kullanıcı kayıt isteği için DTO.
 * Yeni kayıt olan kullanıcılar otomatik MEMBER rolü alır.
 * ADMIN rolü sadece mevcut admin tarafından atanabilir.
 */
@Data
public class RegisterRequest {

    /**
     * Kullanıcının adı.
     * 2-100 karakter arasında olmalı.
     */
    @NotBlank(message = "Ad boş olamaz")
    @Size(min = 2, max = 100, message = "Ad 2-100 karakter arasında olmalıdır")
    private String name;

    /**
     * Kullanıcının soyadı.
     * 2-100 karakter arasında olmalı.
     */
    @NotBlank(message = "Soyad boş olamaz")
    @Size(min = 2, max = 100, message = "Soyad 2-100 karakter arasında olmalıdır")
    private String surname;

    /**
     * Kullanıcının email adresi — sisteme giriş için kullanılır.
     * Sistemde eşsiz olmalı — AuthService'de kontrol edilir.
     */
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    /**
     * Kullanıcının şifresi.
     * BCrypt ile hashlenip saklanır — düz metin asla kaydedilmez.
     */
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;
}