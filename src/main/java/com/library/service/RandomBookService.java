package com.library.service;

import com.library.dto.BookResponse;
import com.library.entity.Book;
import com.library.entity.Rental;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Rastgele kitap öneri sistemini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RandomBookService {

    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;
    private final BookService bookService;

    /**
     * Tamamen rastgele bir kitap seçer.
     */
    public BookResponse getRandomBook() {
        List<Book> books = bookRepository.findByIsActiveTrue();

        if (books.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Sistemde aktif kitap bulunamadı");
        }

        Collections.shuffle(books);
        BookResponse result = bookService.toResponse(books.get(0));

        log.debug("Rastgele kitap seçildi: {}", result.getTitle());

        return result;
    }

    /**
     * Kullanıcının geçmişine göre akıllı kitap önerisi yapar.
     */
    public BookResponse getSmartRandomBook(Long userId) {
        List<Book> allBooks = bookRepository.findByIsActiveTrue();

        if (allBooks.isEmpty()) {
            throw new ResourceNotFoundException("Sistemde aktif kitap bulunamadı");
        }

        Set<Long> readBookIds = rentalRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(r -> r.getBookCopy() != null && r.getBookCopy().getBook() != null)
                .map(r -> r.getBookCopy().getBook().getId())
                .collect(Collectors.toSet());

        List<Book> unreadBooks = allBooks.stream()
                .filter(b -> !readBookIds.contains(b.getId()))
                .collect(Collectors.toList());

        if (unreadBooks.isEmpty()) {
            log.debug("Kullanıcı {} tüm kitapları okumuş, rastgele seçiliyor", userId);

            Collections.shuffle(allBooks);

            return bookService.toResponse(allBooks.get(0));
        }

        Long favoriteCategoryId = findFavoriteCategoryId(userId);

        if (favoriteCategoryId != null) {
            List<Book> categoryUnreadBooks = unreadBooks.stream()
                    .filter(b -> b.getCategory() != null &&
                            b.getCategory().getId().equals(favoriteCategoryId))
                    .collect(Collectors.toList());

            if (!categoryUnreadBooks.isEmpty()) {
                Collections.shuffle(categoryUnreadBooks);
                BookResponse result = bookService.toResponse(categoryUnreadBooks.get(0));

                log.debug("Akıllı öneri (favori kategori): {}", result.getTitle());

                return result;
            }
        }

        Collections.shuffle(unreadBooks);
        BookResponse result = bookService.toResponse(unreadBooks.get(0));

        log.debug("Akıllı öneri (rastgele okunmamış): {}", result.getTitle());

        return result;
    }

    /**
     * Kullanıcının en çok okuduğu kategori ID'sini bulur.
     */
    private Long findFavoriteCategoryId(Long userId) {
        Map<Long, Long> categoryCount = rentalRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(r -> r.getStatus() == Rental.RentalStatus.RETURNED)
                .filter(r -> r.getBookCopy() != null &&
                        r.getBookCopy().getBook() != null &&
                        r.getBookCopy().getBook().getCategory() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getBookCopy().getBook().getCategory().getId(),
                        Collectors.counting()
                ));

        return categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}