package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.entity.*;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookCopyRepository;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kitap yönetimi işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final FindOrCreateService findOrCreateService;
    private final AuditLogService auditLogService;

    /**
     * Tüm aktif kitapları listeler.
     */
    public List<BookResponse> getAllBooks() {
        return bookRepository.findByIsActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * ID'ye göre aktif kitap getirir.
     */
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kitap bulunamadı: ID=" + id));
        return toResponse(book);
    }

    /**
     * Soft delete yapılmış kitapları listeler.
     */
    public List<BookResponse> getDeletedBooks() {
        return bookRepository.findByIsActiveFalse()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Yeni kitap ekler.
     */
    @Transactional
    public BookResponse saveBook(BookRequest request, String userEmail) {
        Book book = new Book();
        BookResponse response = saveOrUpdate(book, request);

        if (request.getCopyCount() != null && request.getCopyCount() > 0) {
            createCopies(book, request.getCopyCount());
        }

        auditLogService.log(userEmail, "BOOK_CREATED", "Book",
                response.getId(), "Kitap eklendi: " + response.getTitle());
        log.info("[{}] Kitap eklendi: {} (ID={})", userEmail, response.getTitle(), response.getId());

        return toResponse(bookRepository.findById(response.getId()).orElse(book));
    }

    /**
     * Mevcut kitabı günceller.
     */
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request, String userEmail) {
        Book book = bookRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kitap bulunamadı: ID=" + id));
        BookResponse response = saveOrUpdate(book, request);
        log.info("[{}] Kitap güncellendi: {} (ID={})", userEmail, response.getTitle(), id);
        return response;
    }

    /**
     * Kitabı soft delete yapar.
     */
    @Transactional
    public void deleteBook(Long id, String userEmail) {
        Book book = bookRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kitap bulunamadı: ID=" + id));
        book.setIsActive(false);
        book.setDeletedAt(LocalDateTime.now());
        bookRepository.save(book);
        log.info("[{}] Kitap silindi (soft): {} (ID={})", userEmail, book.getTitle(), id);
    }

    /**
     * Soft delete yapılmış kitabı geri yükler.
     */
    @Transactional
    public BookResponse restoreBook(Long id, String userEmail) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kitap bulunamadı: ID=" + id));
        book.setIsActive(true);
        book.setDeletedAt(null);
        bookRepository.save(book);
        log.info("[{}] Kitap geri yüklendi: {} (ID={})", userEmail, book.getTitle(), id);
        return toResponse(book);
    }

    /**
     * Birden fazla kitabı aynı anda soft delete yapar.
     */
    @Transactional
    public void bulkDeleteBooks(List<Long> ids, String userEmail) {
        ids.forEach(id ->
                bookRepository.findByIdAndIsActiveTrue(id).ifPresent(book -> {
                    book.setIsActive(false);
                    book.setDeletedAt(LocalDateTime.now());
                    bookRepository.save(book);
                    log.info("[{}] Toplu silme — Kitap: {} (ID={})",
                            userEmail, book.getTitle(), id);
                })
        );
    }

    /**
     * Kitap için otomatik kopya oluşturur.
     */
    private void createCopies(Book book, int count) {
        String prefix = book.getIsbn() != null && !book.getIsbn().isEmpty()
                ? book.getIsbn()
                : "BOOK-" + book.getId();

        int existing = bookCopyRepository.findByBookId(book.getId()).size();

        for (int i = 1; i <= count; i++) {
            BookCopy copy = new BookCopy();
            copy.setBook(book);
            copy.setCopyCode(prefix + "-" + (existing + i));
            copy.setStatus(BookCopy.CopyStatus.AVAILABLE);
            bookCopyRepository.save(copy);
        }
    }

    /**
     * Mevcut kitaba yeni kopyalar ekler.
     */
    @Transactional
    public List<BookCopy> addCopies(Long bookId, int count) {
        Book book = bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı: ID=" + bookId));
        createCopies(book, count);
        return bookCopyRepository.findByBookId(bookId);
    }

    /**
     * Kitabın tüm fiziksel kopyalarını getirir.
     */
    public List<BookCopy> getCopiesByBook(Long bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }

    /**
     * Belirli bir kopyayı siler.
     */
    @Transactional
    public void deleteCopy(Long copyId) {
        bookCopyRepository.deleteById(copyId);
    }

    /**
     * Kitap ekle/güncelle için ortak metod.
     */
    private BookResponse saveOrUpdate(Book book, BookRequest request) {
        book.setTitle(request.getTitle().trim());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setKeywords(request.getKeywords());
        book.setPageCount(request.getPageCount());
        book.setThumbnail(request.getThumbnail());

        book.setAuthor(findOrCreateService.findOrCreateAuthor(request.getAuthorName()));

        book.setPublisher(findOrCreateService.findOrCreatePublisher(request.getPublisherName()));

        book.setCategory(findOrCreateService.findOrCreateCategory(request.getCategoryName()));

        Library library = findOrCreateService.findOrCreateLibrary(
                request.getLibraryName(),
                request.getLibraryAddress(),
                request.getLibraryPhone()
        );

        book.setShelf(findOrCreateService.findOrCreateShelf(
                request.getShelfNumber(),
                request.getShelfSection(),
                library
        ));

        return toResponse(bookRepository.save(book));
    }

    /**
     * Book entity'sini BookResponse DTO'suna dönüştürür.
     */
    public BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());
        response.setDescription(book.getDescription());
        response.setKeywords(book.getKeywords());
        response.setPageCount(book.getPageCount());
        response.setThumbnail(book.getThumbnail());
        response.setIsActive(book.getIsActive());
        response.setCreatedAt(book.getCreatedAt());

        if (book.getAuthor() != null) {
            response.setAuthorName(book.getAuthor().getName());
            response.setAuthorId(book.getAuthor().getId());
        }

        if (book.getPublisher() != null) {
            response.setPublisherName(book.getPublisher().getName());
            response.setPublisherId(book.getPublisher().getId());
        }

        if (book.getCategory() != null) {
            response.setCategoryName(book.getCategory().getName());
            response.setCategoryId(book.getCategory().getId());
        }

        if (book.getShelf() != null) {
            response.setShelfNumber(book.getShelf().getShelfNumber());
            response.setShelfId(book.getShelf().getId());
            if (book.getShelf().getLibrary() != null) {
                response.setLibraryName(book.getShelf().getLibrary().getName());
                response.setLibraryId(book.getShelf().getLibrary().getId());
            }
        }

        List<BookCopy> copies = bookCopyRepository.findByBookId(book.getId());
        response.setTotalCopies(copies.size());
        response.setAvailableCopies(
                (int) copies.stream()
                        .filter(c -> c.getStatus() == BookCopy.CopyStatus.AVAILABLE)
                        .count()
        );

        return response;
    }
}