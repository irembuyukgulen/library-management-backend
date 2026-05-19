package com.library.service;

import com.library.dto.BookFilterRequest;
import com.library.dto.BookResponse;
import com.library.entity.BookCopy;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dinamik kitap filtreleme işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookFilterService {

    private final BookRepository bookRepository;
    private final BookService bookService;

    /**
     * Dinamik filtreleme uygular.
     */
    public List<BookResponse> filterBooks(BookFilterRequest filter) {

        String title = hasText(filter.getTitle()) ? filter.getTitle().trim() : null;
        String keyword = hasText(filter.getKeyword()) ? filter.getKeyword().trim() : null;

        List<BookResponse> books = bookRepository.filterBooks(
                        title,
                        keyword,
                        filter.getAuthorId(),
                        filter.getPublisherId(),
                        filter.getCategoryId(),
                        filter.getLibraryId(),
                        filter.getShelfId()
                ).stream()
                .map(bookService::toResponse)
                .collect(Collectors.toList());

        if (hasText(filter.getStatus())) {
            books = filterByStatus(books, filter.getStatus());
        }

        if (hasText(filter.getSortBy())) {
            books = sortBooks(books, filter.getSortBy(), filter.getSortDir());
        }

        if (filter.getPage() != null && filter.getSize() != null) {
            books = paginate(books, filter.getPage(), filter.getSize());
        }

        log.debug("Dinamik filtreleme sonucu: {} kitap", books.size());
        return books;
    }

    /**
     * Kopya durumuna göre filtreler.
     */
    private List<BookResponse> filterByStatus(List<BookResponse> books, String status) {
        return books.stream()
                .filter(book -> {
                    switch (status.toUpperCase()) {
                        case "AVAILABLE":
                            // En az 1 müsait kopyası olan kitaplar
                            return book.getAvailableCopies() > 0;
                        case "RENTED":
                            // Tüm kopyaları kirada olanlar
                            return book.getAvailableCopies() == 0 &&
                                    book.getTotalCopies() > 0;
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Kitapları sıralar.
     */
    private List<BookResponse> sortBooks(List<BookResponse> books,
                                         String sortBy, String sortDir) {
        Comparator<BookResponse> comparator = switch (sortBy.toLowerCase()) {
            case "author" -> Comparator.comparing(
                    b -> b.getAuthorName() != null ? b.getAuthorName() : "",
                    String.CASE_INSENSITIVE_ORDER);
            case "createdat" -> Comparator.comparing(
                    b -> b.getCreatedAt() != null ? b.getCreatedAt().toString() : "");
            default -> Comparator.comparing(
                    b -> b.getTitle() != null ? b.getTitle() : "",
                    String.CASE_INSENSITIVE_ORDER);
        };

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        return books.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Sayfalama uygular.
     */
    private List<BookResponse> paginate(List<BookResponse> books, int page, int size) {
        int fromIndex = page * size;
        if (fromIndex >= books.size()) {
            return List.of(); // Sayfa aralık dışında
        }
        int toIndex = Math.min(fromIndex + size, books.size());
        return books.subList(fromIndex, toIndex);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}