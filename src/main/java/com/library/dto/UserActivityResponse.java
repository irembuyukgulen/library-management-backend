package com.library.dto;

import lombok.Data;
import java.util.List;

/**
 * Kullanıcı aktivite geçmişi için DTO.
 * Tek endpoint'ten kullanıcının tüm geçmişi döner:
 * - Kiralamalar
 * - Rezervasyonlar
 * - Cezalar
 * - Özet istatistikler
 * Admin herhangi bir kullanıcının aktivitesini görebilir.
 * Üye sadece kendi aktivitesini görebilir.
 */
@Data
public class UserActivityResponse {

    /** Kullanıcı bilgisi */
    private UserResponse user;

    /** Kullanıcının tüm kiralamaları — en yeniden eskiye */
    private List<RentalResponse> rentals;

    /** Kullanıcının tüm rezervasyonları — en yeniden eskiye */
    private List<ReservationResponse> reservations;

    /** Kullanıcının tüm cezaları — en yeniden eskiye */
    private List<PenaltyResponse> penalties;

    /** Özet istatistikler */
    private ActivitySummary summary;

    /**
     * Kullanıcının aktivite özeti.
     * Dashboard kartları için kullanılır.
     */
    @Data
    public static class ActivitySummary {

        /** Toplam kiralama sayısı */
        private int totalRentals;

        /** Aktif (devam eden) kiralama sayısı */
        private int activeRentals;

        /** Gecikmiş kiralama sayısı */
        private int overdueRentals;

        /** Toplam rezervasyon sayısı */
        private int totalReservations;

        /** Aktif rezervasyon sayısı */
        private int activeReservations;

        /** Toplam ceza sayısı */
        private int totalPenalties;
    }
}