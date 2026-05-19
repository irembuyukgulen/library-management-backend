package com.library.service;

import com.library.dto.RentalRequest;
import com.library.dto.RentalResponse;
import com.library.entity.Book;
import com.library.entity.*;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kiralama işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final SystemSettingsRepository systemSettingsRepository;
    private final AuditLogService auditLogService;
    private final WaitlistService waitlistService;
    private final BookRepository bookRepository;

    /**
     * Yeni kiralama başlatır.
     */
    @Transactional
    public RentalResponse createRental(RentalRequest request, String userEmail) {
        Book book = bookRepository.findAll().stream()
                .filter(b -> b.getIsbn() != null &&
                        b.getIsbn().equals(request.getIsbn()) &&
                        b.getIsActive())
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu ISBN ile aktif kitap bulunamadı: " + request.getIsbn()));

        BookCopy copy = bookCopyRepository
                .findByBookIdAndStatus(book.getId(), BookCopy.CopyStatus.AVAILABLE)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("Bu kitabın müsait kopyası yok: " + book.getTitle()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: ID=" + request.getUserId()));

        if (user.getIsBanned()) {
            throw new BusinessException("Bu kullanıcı yasaklıdır: " + user.getBanReason());
        }

        int loanDays = Integer.parseInt(getSettingValue("standard_loan_days", "14"));
        BigDecimal dailyFee = new BigDecimal(getSettingValue("daily_rental_fee", "0"));

        Rental rental = new Rental();
        rental.setBookCopy(copy);
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(loanDays));
        rental.setDailyFee(dailyFee);
        rental.setStatus(Rental.RentalStatus.ACTIVE);

        if (request.getReservationId() != null) {
            Reservation reservation = reservationRepository
                    .findById(request.getReservationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Rezervasyon bulunamadı: ID=" + request.getReservationId()));

            reservation.setStatus(Reservation.ReservationStatus.FULFILLED);
            reservationRepository.save(reservation);
            rental.setReservation(reservation);
        }

        copy.setStatus(BookCopy.CopyStatus.RENTED);
        bookCopyRepository.save(copy);

        Rental saved = rentalRepository.save(rental);

        auditLogService.log(userEmail, "RENTAL_CREATED", "Rental", saved.getId(),
                "Kiralama başlatıldı: " + book.getTitle() + " → " + user.getEmail());

        log.info("[{}] Kiralama başlatıldı: {} → {} (ID={})",
                userEmail, book.getTitle(), user.getEmail(), saved.getId());

        return toResponse(saved);
    }

    /**
     * Kitabı iade eder.
     */
    @Transactional
    public RentalResponse returnRental(Long rentalId, String userEmail) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Kiralama bulunamadı: ID=" + rentalId));

        if (rental.getStatus() != Rental.RentalStatus.ACTIVE &&
                rental.getStatus() != Rental.RentalStatus.OVERDUE) {
            throw new BusinessException("Bu kiralama zaten tamamlanmış");
        }

        rental.setReturnDate(LocalDate.now());
        rental.setStatus(Rental.RentalStatus.RETURNED);

        BookCopy copy = rental.getBookCopy();
        copy.setStatus(BookCopy.CopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

        String bookTitle = copy.getBook() != null ? copy.getBook().getTitle() : "Bilinmiyor";

        if (copy.getBook() != null) {
            waitlistService.notifyWaitingUsers(copy.getBook().getId());
        }

        Rental saved = rentalRepository.save(rental);

        auditLogService.log(userEmail, "RENTAL_RETURNED", "Rental", rentalId,
                "Kitap iade edildi: " + bookTitle);

        log.info("[{}] Kitap iade edildi: {} (KiralamaID={})", userEmail, bookTitle, rentalId);

        return toResponse(saved);
    }

    /**
     * Tüm aktif kiralamaları detaylarıyla getirir.
     */
    public List<RentalResponse> getActiveRentals() {
        return rentalRepository.findActiveRentalsWithDetails()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gecikmiş kiralamaları getirir ve OVERDUE olarak işaretler.
     */
    @Transactional
    public List<RentalResponse> getOverdueRentals() {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getStatus() == Rental.RentalStatus.ACTIVE ||
                        r.getStatus() == Rental.RentalStatus.OVERDUE)
                .filter(r -> r.getDueDate() != null &&
                        r.getDueDate().isBefore(LocalDate.now()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kullanıcının tüm kiralamalarını getirir.
     */
    public List<RentalResponse> getRentalsByUser(Long userId) {
        return rentalRepository.findByUserIdOrderByCreatedAtDesc(userId)
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
     * Rental entity'sini RentalResponse DTO'suna dönüştürür.
     */
    public RentalResponse toResponse(Rental rental) {
        RentalResponse response = new RentalResponse();
        response.setId(rental.getId());
        response.setRentalDate(rental.getRentalDate());
        response.setDueDate(rental.getDueDate());
        response.setReturnDate(rental.getReturnDate());
        response.setDailyFee(rental.getDailyFee());
        response.setStatus(rental.getStatus().name());
        response.setCreatedAt(rental.getCreatedAt());

        if (rental.getBookCopy() != null) {
            response.setCopyCode(rental.getBookCopy().getCopyCode());

            if (rental.getBookCopy().getBook() != null) {
                response.setBookTitle(rental.getBookCopy().getBook().getTitle());
            }
        }

        if (rental.getUser() != null) {
            response.setUserName(rental.getUser().getName() + " " + rental.getUser().getSurname());
            response.setUserEmail(rental.getUser().getEmail());
            response.setUserId(rental.getUser().getId());
        }

        calculateOverdueAndFee(rental, response);

        return response;
    }

    /**
     * Gecikme gün sayısı ve toplam ücreti hesaplar.
     */
    private void calculateOverdueAndFee(Rental rental, RentalResponse response) {
        LocalDate endDate = rental.getReturnDate() != null
                ? rental.getReturnDate()
                : LocalDate.now();

        if (endDate.isAfter(rental.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(rental.getDueDate(), endDate);
            response.setDaysOverdue(daysOverdue);

            BigDecimal lateFeePerDay = new BigDecimal(
                    getSettingValue("daily_late_fee", "0"));

            long rentalDays = ChronoUnit.DAYS.between(
                    rental.getRentalDate(), rental.getDueDate());
            BigDecimal normalFee = rental.getDailyFee()
                    .multiply(BigDecimal.valueOf(rentalDays));

            BigDecimal penaltyFee = lateFeePerDay.multiply(BigDecimal.valueOf(daysOverdue));
            response.setTotalFee(normalFee.add(penaltyFee));
        } else {
            response.setDaysOverdue(0L);
            long days = ChronoUnit.DAYS.between(rental.getRentalDate(), endDate);
            response.setTotalFee(rental.getDailyFee().multiply(BigDecimal.valueOf(days)));
        }
    }
}