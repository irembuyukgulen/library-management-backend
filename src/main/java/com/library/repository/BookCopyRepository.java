package com.library.repository;

import com.library.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Kitap kopyası veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    /**
     * Belirli bir kitabın tüm kopyalarını getirir.
     * Kitap detay sayfasında kopya listesi için kullanılır.
     */
    List<BookCopy> findByBookId(Long bookId);

    /**
     * Belirli bir kitabın belirli durumdaki kopyalarını getirir.
     */
    List<BookCopy> findByBookIdAndStatus(Long bookId, BookCopy.CopyStatus status);

    /**
     * Tüm sistemdeki belirli durumdaki kopyaları getirir.
     * İstatistik ve raporlama için kullanılır.
     */
    List<BookCopy> findByStatus(BookCopy.CopyStatus status);

    /**
     * Belirli bir kitabın belirli durumdaki kopyalarını sayar.
     * Müsait kopya sayısını hızlıca bulmak için kullanılır.
     */
    int countByBookIdAndStatus(Long bookId, BookCopy.CopyStatus status);
}