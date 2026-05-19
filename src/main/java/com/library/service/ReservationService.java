package com.library.service;

import com.library.dto.ReservationRequest;
import com.library.dto.ReservationResponse;
import com.library.entity.*;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rezervasyon işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SystemSettingsRepository systemSettingsRepository;

    /**
     * Yeni rezervasyon oluşturur.
     */
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        Book book = bookRepository.findByIdAndIsActiveTrue(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı: ID=" + request.getBookId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: ID=" + request.getUserId()));

        if (user.getIsBanned()) {
            throw new BusinessException("Bu kullanıcı yasaklıdır: " + user.getBanReason());
        }

        List<Reservation> existingReservations = reservationRepository
                .findByUserIdAndStatus(user.getId(), Reservation.ReservationStatus.ACTIVE);

        boolean alreadyReserved = existingReservations.stream()
                .anyMatch(r -> r.getBook().getId().equals(book.getId()));

        if (alreadyReserved) {
            throw new BusinessException("Bu kitap için zaten aktif bir rezervasyonunuz var: " + book.getTitle());
        }

        int reservationDays = Integer.parseInt(getSettingValue("reservation_days", "7"));

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setReservationDate(LocalDate.now());
        reservation.setExpiryDate(LocalDate.now().plusDays(reservationDays));
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);

        Reservation saved = reservationRepository.save(reservation);

        log.info("Rezervasyon oluşturuldu: {} → {} (ID={})", book.getTitle(), user.getEmail(), saved.getId());

        return toResponse(saved);
    }

    /**
     * Rezervasyonu iptal eder.
     */
    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervasyon bulunamadı: ID=" + id));

        if (reservation.getStatus() != Reservation.ReservationStatus.ACTIVE) {
            throw new BusinessException("Bu rezervasyon zaten aktif değil. Durum: " + reservation.getStatus());
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        Reservation saved = reservationRepository.save(reservation);

        log.info("Rezervasyon iptal edildi: ID={}", id);

        return toResponse(saved);
    }

    /**
     * Süresi dolmuş aktif rezervasyonları EXPIRED olarak işaretler.
     */
    @Transactional
    public void expireReservations() {
        List<Reservation> activeReservations = reservationRepository
                .findByStatus(Reservation.ReservationStatus.ACTIVE);

        long expiredCount = activeReservations.stream()
                .filter(r -> r.getExpiryDate() != null &&
                        r.getExpiryDate().isBefore(LocalDate.now()))
                .peek(r -> {
                    r.setStatus(Reservation.ReservationStatus.EXPIRED);
                    reservationRepository.save(r);
                }).count();

        if (expiredCount > 0) {
            log.info("{} adet rezervasyon süresi doldu ve EXPIRED olarak işaretlendi", expiredCount);
        }
    }

    /**
     * Kullanıcının tüm rezervasyonlarını getirir.
     */
    public List<ReservationResponse> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir kitabın aktif rezervasyonlarını getirir.
     */
    public List<ReservationResponse> getActiveReservationsByBook(Long bookId) {
        return reservationRepository
                .findByBookIdAndStatus(bookId, Reservation.ReservationStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tüm rezervasyonları getirir.
     */
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Sistem ayarından değer okur.
     */
    private String getSettingValue(String key, String defaultValue) {
        return systemSettingsRepository.findBySettingKey(key)
                .map(SystemSettings::getSettingValue)
                .orElse(defaultValue);
    }

    /**
     * Reservation entity'sini ReservationResponse DTO'suna dönüştürür.
     */
    public ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setReservationDate(reservation.getReservationDate());
        response.setExpiryDate(reservation.getExpiryDate());
        response.setStatus(reservation.getStatus().name());
        response.setCreatedAt(reservation.getCreatedAt());

        if (reservation.getBook() != null) {
            response.setBookTitle(reservation.getBook().getTitle());
            response.setBookId(reservation.getBook().getId());
        }

        if (reservation.getUser() != null) {
            response.setUserName(reservation.getUser().getName() + " " + reservation.getUser().getSurname());
            response.setUserEmail(reservation.getUser().getEmail());
            response.setUserId(reservation.getUser().getId());
        }

        return response;
    }
}