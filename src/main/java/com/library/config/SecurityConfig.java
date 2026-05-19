package com.library.config;

import com.library.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security yapılandırma sınıfı.
 * Temel güvenlik kararları:
 * 1. JWT tabanlı authentication (session yok)
 * 2. URL pattern'i ile yetkilendirme (/api/admin/** → sadece ADMIN)
 * 3. CORS yapılandırması (React frontend erişimi için)
 * 4. BCrypt şifre hashleme
 * Endpoint yetkilendirme tablosu:
 * /api/auth/**          → Herkese açık (login, register)
 * /api/admin/**         → Sadece ADMIN
 * GET /api/books/**     → Giriş yapmış herkes
 * GET /api/authors      → Giriş yapmış herkes
 * GET /api/publishers   → Giriş yapmış herkes
 * GET /api/categories   → Giriş yapmış herkes
 * GET /api/libraries    → Giriş yapmış herkes
 * GET /api/shelves      → Giriş yapmış herkes
 * Diğerleri             → Giriş yapmış herkes
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // CORS yapılandırmasını etkinleştir
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Session kullanma — JWT stateless
                // Her istek kendi token'ını taşır, sunucu session saklamaz
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint yetkilendirme kuralları
                .authorizeHttpRequests(auth -> auth

                        // Login ve register herkese açık
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // Admin endpoint'leri sadece ADMIN rolüne açık
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Kitap listeleme ve arama herkese açık (giriş yapılmış)
                        .requestMatchers(HttpMethod.GET, "/api/books/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/authors/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/publishers/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/libraries/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/shelves/**").authenticated()

                        // Geri kalan tüm istekler giriş gerektirir
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt şifre encoder — şifreleri hash'lemek için.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * CORS (Cross-Origin Resource Sharing) yapılandırması.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // İzin verilen originler — production'da gerçek domain yaz
        configuration.setAllowedOriginPatterns(List.of("*"));

        // İzin verilen HTTP metodları
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // İzin verilen header'lar
        configuration.setAllowedHeaders(List.of("*"));

        // Authorization header'ının response'da görünmesine izin ver
        configuration.setExposedHeaders(List.of("Authorization"));

        // Cookie göndermeye izin ver (gerekirse)
        configuration.setAllowCredentials(true);

        // Tüm endpoint'lere uygula
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}