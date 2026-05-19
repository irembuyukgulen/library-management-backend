package com.library.service;

import com.library.dto.AuthRequest;
import com.library.dto.AuthResponse;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.UserRepository;
import com.library.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kimlik doğrulama işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Yeni kullanıcı kaydı oluşturur.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Email daha önce kullanılmış mı?
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Bu email adresi zaten kayıtlı: " + request.getEmail());
        }

        // Yeni kullanıcı oluştur
        User user = new User();
        user.setName(request.getName().trim());
        user.setSurname(request.getSurname().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.MEMBER);
        user.setIsBanned(false);

        userRepository.save(user);
        log.info("Yeni kullanıcı kaydoldu: {}", user.getEmail());

        return buildAuthResponse(user);
    }

    /**
     * Kullanıcı girişini doğrular ve JWT token döndürür.
     */
    public AuthResponse login(AuthRequest request) {

        // Kullanıcıyı email ile bul
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu email adresiyle kayıtlı kullanıcı bulunamadı"));

        // Şifre doğru mu?
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Başarısız giriş denemesi: {}", request.getEmail());
            throw new BusinessException("Email veya şifre hatalı");
        }

        // Yasaklı kullanıcı uyarısı
        if (user.getIsBanned()) {
            log.warn("Yasaklı kullanıcı giriş yaptı: {}", user.getEmail());
            throw new BusinessException("Hesabınız yasaklanmıştır. Sebep: " + user.getBanReason());
        }

        log.info("Kullanıcı giriş yaptı: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    /**
     * Kullanıcı bilgilerinden AuthResponse oluşturur.
     */
    private AuthResponse buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getId()
        );
    }
}