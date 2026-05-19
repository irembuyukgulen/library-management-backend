package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Başarılı giriş veya kayıt sonrası dönen yanıt DTO'su.
 * Frontend'e şu formatta döner:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "role": "ADMIN",
 *   "email": "admin@library.com",
 *   "name": "Admin",
 *   "userId": 1
 * }
 * Frontend bu bilgileri localStorage'a kaydeder.
 * Token her API isteğinde "Authorization: Bearer TOKEN" header'ında gönderilir.
 * Role göre yönlendirme yapılır: ADMIN → /admin/dashboard, MEMBER → /member/books
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    /** JWT token — her API isteğinde kullanılır */
    private String token;

    /** Kullanıcının rolü: ADMIN veya MEMBER */
    private String role;

    /** Kullanıcının email adresi */
    private String email;

    /** Kullanıcının adı — karşılama mesajı için */
    private String name;

    /** Kullanıcının adı — karşılama mesajı için */
    private String surname;

    /** Kullanıcının ID'si — aktivite ve bildirim sorgularında kullanılır */
    private Long userId;
}