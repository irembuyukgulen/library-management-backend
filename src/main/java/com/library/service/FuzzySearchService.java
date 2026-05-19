package com.library.service;

import com.library.dto.BookResponse;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bulanık (fuzzy) arama işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FuzzySearchService {

    private final BookRepository bookRepository;
    private final BookService bookService;

    /**
     * Benzerlik yüzdesi eşiği.
     */
    private static final int THRESHOLD = 60;

    /**
     * Fuzzy arama yapar.
     */
    public List<BookResponse> fuzzySearchBooks(String query) {

        if (query == null || query.trim().isEmpty()) {
            return bookService.getAllBooks();
        }

        String normalizedQuery = query.toLowerCase().trim();
        List<Book> allBooks = bookRepository.findByIsActiveTrue();

        List<BookResponse> results = allBooks.stream()
                .filter(book -> isMatch(book, normalizedQuery))
                .map(bookService::toResponse)
                .collect(Collectors.toList());

        log.debug("Fuzzy arama: '{}' → {} sonuç", query, results.size());
        return results;
    }

    /**
     * Kitabın sorguyla eşleşip eşleşmediğini kontrol eder.
     */
    private boolean isMatch(Book book, String query) {

        int titleScore = FuzzySearch.partialRatio(
                query, book.getTitle().toLowerCase());

        int authorScore = 0;
        if (book.getAuthor() != null) {
            authorScore = FuzzySearch.partialRatio(
                    query, book.getAuthor().getName().toLowerCase());
        }

        int keywordScore = 0;
        if (book.getKeywords() != null && !book.getKeywords().isEmpty()) {
            keywordScore = FuzzySearch.partialRatio(
                    query, book.getKeywords().toLowerCase());
        }

        // Herhangi biri eşik değerini geçiyorsa eşleşme var
        return titleScore >= THRESHOLD
                || authorScore >= THRESHOLD
                || keywordScore >= THRESHOLD;
    }
}