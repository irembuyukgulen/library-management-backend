package com.library.service;

import com.library.dto.*;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kullanıcı aktivite geçmişini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;
    private final PenaltyRepository penaltyRepository;
    private final UserService userService;
    private final RentalService rentalService;
    private final ReservationService reservationService;
    private final PenaltyService penaltyService;

    /**
     * Kullanıcının tüm aktivite geçmişini getirir.
     */
    public UserActivityResponse getUserActivity(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: ID=" + userId));

        UserActivityResponse activity = new UserActivityResponse();
        activity.setUser(userService.getUserById(userId));

        List<RentalResponse> rentals = rentalRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(rentalService::toResponse)
                .collect(Collectors.toList());
        activity.setRentals(rentals);

        List<ReservationResponse> reservations = reservationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(reservationService::toResponse)
                .collect(Collectors.toList());
        activity.setReservations(reservations);

        List<PenaltyResponse> penalties = penaltyRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(penaltyService::toResponse)
                .collect(Collectors.toList());
        activity.setPenalties(penalties);

        activity.setSummary(buildSummary(rentals, reservations, penalties));

        return activity;
    }

    /**
     * Aktivite özetini hesaplar.
     */
    private UserActivityResponse.ActivitySummary buildSummary(
            List<RentalResponse> rentals,
            List<ReservationResponse> reservations,
            List<PenaltyResponse> penalties) {

        UserActivityResponse.ActivitySummary summary =
                new UserActivityResponse.ActivitySummary();

        summary.setTotalRentals(rentals.size());

        summary.setActiveRentals((int) rentals.stream()
                .filter(r -> "ACTIVE".equals(r.getStatus()) ||
                        "OVERDUE".equals(r.getStatus()))
                .count());

        summary.setOverdueRentals((int) rentals.stream()
                .filter(r -> r.getDueDate() != null &&
                        r.getDueDate().isBefore(LocalDate.now()) &&
                        ("ACTIVE".equals(r.getStatus()) ||
                                "OVERDUE".equals(r.getStatus())))
                .count());

        summary.setTotalReservations(reservations.size());

        summary.setActiveReservations((int) reservations.stream()
                .filter(r -> "ACTIVE".equals(r.getStatus()))
                .count());

        summary.setTotalPenalties(penalties.size());

        return summary;
    }
}