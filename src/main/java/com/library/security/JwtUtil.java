package com.library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT (JSON Web Token) işlemlerini yöneten yardımcı sınıf.
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * Token imzalama için kullanılan gizli anahtar.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token geçerlilik süresi (millisecond).
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Kullanıcı için JWT token üretir.
     */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Token'dan kullanıcının email adresini çıkarır.
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Token'dan kullanıcının rolünü çıkarır.
     */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * Token'ın geçerli olup olmadığını kontrol eder.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT token süresi dolmuş: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("Desteklenmeyen JWT token: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Hatalı JWT token formatı: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.warn("JWT imza hatası: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT token boş: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Token'ı parse eder ve claims (içerik) döner.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // İmzayı doğrula
                .build()
                .parseSignedClaims(token)      // Token'ı parse et
                .getPayload();                 // İçeriği al
    }
}