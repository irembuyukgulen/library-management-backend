package com.library.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Kullanıcı bilgisi döndürmek için DTO.
 * Entity'deki passwordHash alanı burada YOK.
 * Şifre hash'i asla API'den döndürülmez — güvenlik.
 * Admin panelinde kullanıcı listesi ve detay sayfası için kullanılır.
 * Üye kendi profilini görüntülemek için de kullanılır.
 */
@Data
public class UserResponse {

    /** Kullanıcının ID'si */
    private Long id;

    /** Kullanıcının adı */
    private String name;

    /** Kullanıcının soyadı */
    private String surname;

    /** Kullanıcının email adresi */
    private String email;

    /** Kullanıcının rolü: ADMIN veya MEMBER */
    private String role;

    /**
     * Kullanıcının yasaklı olup olmadığı.
     * true → yasaklı, kiralama ve rezervasyon yapamaz
     */
    private Boolean isBanned;

    /** Yasaklama sebebi — isBanned true ise dolu */
    private String banReason;

    /** Kayıt tarihi */
    private LocalDateTime createdAt;
}