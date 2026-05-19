package com.library.repository;

import com.library.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Kiralama veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    /**
     * Kullanıcının belirli durumdaki kiralamasını sırasız getirir.
     */
    List<Rental> findByUserId(Long userId);

    /**
     * Kullanıcının tüm kiralamalarını getirir — tarih sırasıyla.
     */
    List<Rental> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Kullanıcının belirli durumdaki kiralamalarını getirir.
     */
    List<Rental> findByUserIdAndStatus(Long userId, Rental.RentalStatus status);

    /**
     * Belirli durumdaki ve iade tarihi geçmiş kiralamaları getirir.
     */
    List<Rental> findByStatusAndDueDateBefore(Rental.RentalStatus status, LocalDate date);

    /**
     * Belirli durumdaki kiralamaları sayar.
     */
    long countByStatus(Rental.RentalStatus status);

    void deleteByUserId(Long userId);

    /**
     * Aktif kiralamaları detaylarıyla getirir.
     */
    @Query("SELECT r FROM Rental r " +
            "JOIN FETCH r.bookCopy bc " +
            "JOIN FETCH bc.book b " +
            "JOIN FETCH r.user u " +
            "WHERE r.status = 'ACTIVE' " +
            "ORDER BY r.dueDate ASC")
    List<Rental> findActiveRentalsWithDetails();

    /**
     * En aktif üyeleri getirir — istatistik için.
     */
    @Query("SELECT u.name, u.surname, u.email, COUNT(r) as rentalCount " +
            "FROM Rental r JOIN r.user u " +
            "GROUP BY u.name, u.surname, u.email " +
            "ORDER BY rentalCount DESC")
    List<Object[]> findMostActiveMembers();
}