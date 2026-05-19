package com.library.service;

import com.library.dto.NotificationResponse;
import com.library.entity.Rental;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.RentalRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Kira süresi bildirimlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private static final int WARNING_DAYS = 3;

    /**
     * Kullanıcının aktif kiralamalarını kontrol eder ve bildirim listesi döndürür.
     */
    public List<NotificationResponse> getNotificationsForUser(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: ID=" + userId));

        // Sadece aktif kiralamaları kontrol eder
        List<Rental> activeRentals = new ArrayList<>();
        activeRentals.addAll(rentalRepository.findByUserIdAndStatus(userId, Rental.RentalStatus.ACTIVE));
        activeRentals.addAll(rentalRepository.findByUserIdAndStatus(userId, Rental.RentalStatus.OVERDUE));

        List<NotificationResponse> notifications = new ArrayList<>();

        for (Rental rental : activeRentals) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), rental.getDueDate());

            NotificationResponse notification = new NotificationResponse();
            notification.setRentalId(rental.getId());
            notification.setDueDate(rental.getDueDate());
            notification.setDaysRemaining(daysRemaining);

            if (rental.getBookCopy() != null && rental.getBookCopy().getBook() != null) {
                notification.setBookTitle(rental.getBookCopy().getBook().getTitle());
            }

            if (daysRemaining < 0) {
                notification.setType("OVERDUE");
                notification.setMessage("\"" + notification.getBookTitle() +
                        "\" kitabının iade süresi " + Math.abs(daysRemaining) + " gün geçti!");
                notifications.add(notification);
            } else if (daysRemaining <= WARNING_DAYS) {
                notification.setType("WARNING");
                notification.setMessage("\"" + notification.getBookTitle() +
                        "\" kitabının iade süresine " + daysRemaining + " gün kaldı!");
                notifications.add(notification);
            }
        }

        return notifications;
    }
}