package com.library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Her HTTP isteğinde bir kez çalışan JWT doğrulama filtresi.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /** Authorization header'ının adı */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /** Bearer token prefix'i */
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Header'dan token'ı al
            String token = extractTokenFromRequest(request);

            // Token varsa ve geçerliyse kullanıcıyı authenticate et
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                authenticateUser(token, request);
            }

        } catch (Exception ex) {
            // Hata fırlatma — loglayıp devam et
            // SecurityConfig gerekli endpoint'leri zaten korur
            log.error("JWT filter hatası: {}", ex.getMessage());
        }

        // Her durumda isteği devam ettir
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP isteğinin Authorization header'ından token'ı çıkarır.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Token'dan kullanıcı bilgisini çıkarır ve
     */
    private void authenticateUser(String token, HttpServletRequest request) {
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Kullanıcı authenticate edildi: {}, rol: {}", email, role);
    }
}