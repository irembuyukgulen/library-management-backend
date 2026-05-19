package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Sistemdeki kullanıcıları temsil eder.
 * İki rol vardır: ADMIN (yönetici) ve MEMBER (üye).
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /** Otomatik artan birincil anahtar */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Kullanıcının adı — boş olamaz */
    @Column(nullable = false)
    private String name;

    /** Kullanıcının soyadı — boş olamaz */
    @Column(nullable = false)
    private String surname;

    /** E-posta adresi — benzersiz olmalı, giriş için kullanılır */
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt ile hashlenmiş şifre — düz metin asla saklanmaz */
    @Column(nullable = false)
    private String passwordHash;

    /** Kullanıcı rolü — ADMIN veya MEMBER */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Kullanıcının yasaklı olup olmadığı — varsayılan false */
    @Column(nullable = false)
    private Boolean isBanned = false;

    /** Yasaklanma gerekçesi — isBanned true ise dolu olmalı */
    private String banReason;

    /** Kayıt oluşturulma tarihi — güncellenmez */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Veritabanına kaydedilmeden önce otomatik çalışır.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Kullanıcı rolleri.
     * ADMIN: Tüm sistem yönetimi yetkisine sahip.
     * MEMBER: Kitap arama, kiralama ve rezervasyon yapabilir.
     */
    public enum Role {
        ADMIN,
        MEMBER
    }
}