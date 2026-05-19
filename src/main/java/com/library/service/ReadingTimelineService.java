package com.library.service;

import com.library.dto.ReadingTimelineResponse;
import com.library.entity.Rental;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.RentalRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Kullanıcının okuma zaman tünelini oluşturan servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingTimelineService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Kullanıcının okuma zaman tünelini oluşturur.
     */
    public ReadingTimelineResponse getTimeline(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: ID=" + userId));

        List<Rental> returnedRentals = rentalRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(r -> r.getStatus() == Rental.RentalStatus.RETURNED &&
                        r.getReturnDate() != null)
                .collect(Collectors.toList());

        ReadingTimelineResponse response = new ReadingTimelineResponse();
        int currentYear = LocalDate.now().getYear();

        Map<String, Integer> monthlyPageMap = new TreeMap<>();
        Map<String, Integer> monthlyBookMap = new TreeMap<>();

        Map<String, Integer> categoryCount = new HashMap<>();

        int currentYearBooks = 0, lastYearBooks = 0;
        int currentYearPages = 0, lastYearPages = 0;
        int totalPages = 0;
        String longestBookTitle = null;
        int longestBookPages = 0;

        for (Rental rental : returnedRentals) {
            int year = rental.getReturnDate().getYear();
            String monthKey = rental.getReturnDate().format(MONTH_FORMATTER);

            int pages = 0;
            String bookTitle = "";
            String categoryName = "";

            if (rental.getBookCopy() != null && rental.getBookCopy().getBook() != null) {
                var book = rental.getBookCopy().getBook();
                pages = book.getPageCount() != null ? book.getPageCount() : 0;
                bookTitle = book.getTitle() != null ? book.getTitle() : "";

                if (book.getCategory() != null) {
                    categoryName = book.getCategory().getName();
                }
            }

            if (year == currentYear) {
                currentYearBooks++;
                currentYearPages += pages;
            } else if (year == currentYear - 1) {
                lastYearBooks++;
                lastYearPages += pages;
            }

            monthlyPageMap.merge(monthKey, pages, Integer::sum);
            monthlyBookMap.merge(monthKey, 1, Integer::sum);

            if (!categoryName.isEmpty()) {
                categoryCount.merge(categoryName, 1, Integer::sum);
            }

            if (pages > longestBookPages && !bookTitle.isEmpty()) {
                longestBookPages = pages;
                longestBookTitle = bookTitle;
            }

            totalPages += pages;
        }

        response.setCurrentYearBooks(currentYearBooks);
        response.setLastYearBooks(lastYearBooks);
        response.setCurrentYearPages(currentYearPages);
        response.setLastYearPages(lastYearPages);
        response.setTotalBooksRead(returnedRentals.size());
        response.setTotalPagesRead(totalPages);
        response.setMonthlyPageMap(monthlyPageMap);
        response.setMonthlyBookMap(monthlyBookMap);
        response.setLongestBookTitle(longestBookTitle);
        response.setLongestBookPages(longestBookPages > 0 ? longestBookPages : null);

        categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> response.setFavoriteCategory(e.getKey()));

        if (lastYearBooks > 0) {
            double improvement = ((double) (currentYearBooks - lastYearBooks) / lastYearBooks) * 100;
            response.setImprovementPercent(Math.round(improvement * 10.0) / 10.0);
        }

        response.setMotivationMessage(buildMotivationMessage(currentYearBooks, response.getImprovementPercent()));

        return response;
    }

    /**
     * Okuma istatistiklerine göre motivasyon mesajı üretir.
     */
    private String buildMotivationMessage(int books, double improvement) {
        if (books == 0) {
            return "Henüz hiç kitap okumadınız. İlk kitabınızı seçin! 📚";
        }

        if (books == 1) {
            return "İlk kitabınızı bitirdiniz! Harika bir başlangıç! 🎉";
        }

        if (improvement > 20) {
            return "Bu yıl " + books + " kitap bitirdiniz, geçen yıla göre %" + improvement + " daha hızlısınız! 🚀";
        }

        if (improvement > 0) {
            return "Bu yıl " + books + " kitap bitirdiniz. Geçen yıla göre ilerliyorsunuz! 💪";
        }

        if (improvement < 0) {
            return "Bu yıl " + books + " kitap bitirdiniz. Geçen yıla yetişelim! 💪";
        }

        return "Bu yıl " + books + " kitap bitirdiniz. Muhteşem! ⭐";
    }
}