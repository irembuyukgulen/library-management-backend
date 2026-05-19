package com.library.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Admin dashboard istatistik paneli için DTO.
 * Tek bir endpoint'ten tüm istatistikler döner.
 * Frontend bu veriyle chart ve kart gösterir.
 */
@Data
public class StatisticsResponse {

    /** Toplam aktif kitap sayısı */
    private long totalBooks;

    /** Toplam fiziksel kopya sayısı */
    private long totalCopies;

    /** Şu an müsait kopya sayısı */
    private long availableCopies;

    /** Şu an kirada olan kopya sayısı */
    private long rentedCopies;

    /** Toplam üye sayısı (ADMIN hariç) */
    private long totalMembers;

    /** Aktif kiralama sayısı */
    private long activeRentals;

    /** Gecikmiş kiralama sayısı */
    private long overdueRentals;

    /** Toplam rezervasyon sayısı */
    private long totalReservations;

    /** Aktif rezervasyon sayısı */
    private long activeReservations;

    /**
     * En çok kiralanan kitaplar — top 5.
     */
    private List<Map<String, Object>> mostRentedBooks;

    /**
     * En aktif üyeler — top 5.
     */
    private List<Map<String, Object>> mostActiveMembers;
}