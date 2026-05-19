package com.library.repository;

import com.library.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Rezervasyon veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Kullanıcının tüm rezervasyonlarını getirir — tarih sırasıyla.
     */
    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Kullanıcının belirli durumdaki rezervasyonlarını getirir.
     */
    List<Reservation> findByUserIdAndStatus(Long userId, Reservation.ReservationStatus status);

    /**
     * Belirli bir kitabın belirli durumdaki rezervasyonlarını getirir.
     */
    List<Reservation> findByBookIdAndStatus(Long bookId, Reservation.ReservationStatus status);

    /**
     * Tüm aktif rezervasyonları getirir.
     */
    List<Reservation> findByStatus(Reservation.ReservationStatus status);

    void deleteByUserId(Long userId);
}