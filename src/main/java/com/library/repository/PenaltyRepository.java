package com.library.repository;

import com.library.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Ceza veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    /**
     * Kullanıcının tüm cezalarını getirir — tarih sırasıyla.
     */
    List<Penalty> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Belirli bir kiralamaya ait cezaları getirir.
     */
    List<Penalty> findByRentalId(Long rentalId);

    void deleteByUserId(Long userId);
}