package com.library.service;

import com.library.dto.StatisticsResponse;
import com.library.entity.*;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin dashboard istatistik panelini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Tüm istatistikleri toplar ve döndürür.
     */
    public StatisticsResponse getStatistics() {
        StatisticsResponse stats = new StatisticsResponse();

        stats.setTotalBooks(bookRepository.findByIsActiveTrue().size());

        stats.setTotalCopies(bookCopyRepository.count());
        stats.setAvailableCopies(bookCopyRepository.findByStatus(BookCopy.CopyStatus.AVAILABLE).size());
        stats.setRentedCopies(bookCopyRepository.findByStatus(BookCopy.CopyStatus.RENTED).size());

        stats.setTotalMembers(userRepository.findByRole(User.Role.MEMBER).size());

        stats.setActiveRentals(rentalRepository.countByStatus(Rental.RentalStatus.ACTIVE));
        stats.setOverdueRentals(rentalRepository
                .findByStatusAndDueDateBefore(Rental.RentalStatus.ACTIVE, LocalDate.now()).size());

        stats.setTotalReservations(reservationRepository.count());
        stats.setActiveReservations(reservationRepository
                .findByStatus(Reservation.ReservationStatus.ACTIVE).size());

        stats.setMostRentedBooks(buildMostRentedBooks());
        stats.setMostActiveMembers(buildMostActiveMembers());

        return stats;
    }

    /** En çok kiralanan 5 kitabı listeler */
    private List<Map<String, Object>> buildMostRentedBooks() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Object[]> rawData = bookRepository.findMostRentedBooks();

        for (int i = 0; i < Math.min(5, rawData.size()); i++) {
            Object[] row = rawData.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("title", row[0]);
            item.put("rentalCount", row[1]);
            result.add(item);
        }

        return result;
    }

    /** En aktif 5 üyeyi listeler */
    private List<Map<String, Object>> buildMostActiveMembers() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Object[]> rawData = rentalRepository.findMostActiveMembers();

        for (int i = 0; i < Math.min(5, rawData.size()); i++) {
            Object[] row = rawData.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("name", row[0] + " " + row[1]);
            item.put("email", row[2]);
            item.put("rentalCount", row[3]);
            result.add(item);
        }

        return result;
    }
}