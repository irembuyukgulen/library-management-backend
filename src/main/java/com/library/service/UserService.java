package com.library.service;

import com.library.dto.UserResponse;
import com.library.dto.UpdateProfileRequest;
import com.library.dto.UpdatePasswordRequest;
import com.library.entity.User;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.UserRepository;
import com.library.repository.ReservationRepository;
import com.library.repository.RentalRepository;
import com.library.repository.PenaltyRepository;
import com.library.repository.WaitlistNotificationRepository;
import com.library.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Kullanıcı yönetimi işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;
    private final RentalRepository rentalRepository;
    private final PenaltyRepository penaltyRepository;
    private final WaitlistNotificationRepository waitlistNotificationRepository;
    private final AuditLogRepository auditLogRepository;

    /**
     * Tüm kullanıcıları listeler.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * ID'ye göre kullanıcı getirir.
     */
    public UserResponse getUserById(Long id) {
        return toResponse(findUserById(id));
    }

    /**
     * Email adresine göre kullanıcı getirir.
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + email));

        return toResponse(user);
    }

    /**
     * Kullanıcıyı yasaklar.
     */
    @Transactional
    public UserResponse banUser(Long id, String reason) {
        User user = findUserById(id);

        if (user.getRole() == User.Role.ADMIN) {
            throw new BusinessException("Admin kullanıcılar yasaklanamaz");
        }

        if (user.getIsBanned()) {
            throw new BusinessException("Bu kullanıcı zaten yasaklı");
        }

        user.setIsBanned(true);
        user.setBanReason(reason);
        userRepository.save(user);

        log.info("Kullanıcı yasaklandı: {} — Sebep: {}", user.getEmail(), reason);

        return toResponse(user);
    }

    /**
     * Kullanıcının yasağını kaldırır.
     */
    @Transactional
    public UserResponse unbanUser(Long id) {
        User user = findUserById(id);

        if (!user.getIsBanned()) {
            throw new BusinessException("Bu kullanıcı zaten yasaklı değil");
        }

        user.setIsBanned(false);
        user.setBanReason(null);
        userRepository.save(user);

        log.info("Kullanıcı yasağı kaldırıldı: {}", user.getEmail());

        return toResponse(user);
    }

    /**
     * Kullanıcıyı siler.
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + userId));

        // İlişkili kayıtları önce sil
        reservationRepository.deleteByUserId(userId);
        rentalRepository.deleteByUserId(userId);
        penaltyRepository.deleteByUserId(userId);
        waitlistNotificationRepository.deleteByUserId(userId);
        auditLogRepository.deleteByUserEmail(user.getEmail());

        userRepository.delete(user);
    }

    /**
     * ID'ye göre kullanıcıyı bulur.
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException( "Kullanıcı bulunamadı: ID=" + id));
    }

    /**
     * Kullanıcının profil bilgilerini günceller.
     */
    @Transactional
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = findUserById(id);

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Bu email adresi zaten kullanılıyor");
        }

        user.setName(request.getName().trim());
        user.setSurname(request.getSurname().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());

        return toResponse(userRepository.save(user));
    }

    /**
     * Kullanıcının şifresini günceller.
     */
    @Transactional
    public void updatePassword(Long id, UpdatePasswordRequest request) {
        User user = findUserById(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("Mevcut şifre hatalı");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Şifre güncellendi: {}", user.getEmail());
    }

    /**
     * User entity'sini UserResponse DTO'suna dönüştürür.
     */
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setSurname(user.getSurname());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setIsBanned(user.getIsBanned());
        response.setBanReason(user.getBanReason());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }
}