package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Kitap veritabanı işlemlerini yöneten Repository.
 * Önemli: Soft delete uygulandığı için tüm sorgular
 * "isActive = true" koşulunu içerir.
 * Silinmiş kitaplar listelenmez ama veritabanında durur.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Sadece aktif (silinmemiş) kitapları getirir.
     * Ana kitap listeleme için kullanılır.
     */
    List<Book> findByIsActiveTrue();

    /**
     * Aktif kitap ID'ye göre bulur.
     * Soft delete kontrolü yapılır — silinmiş kitap bulunamaz.
     */
    Optional<Book> findByIdAndIsActiveTrue(Long id);

    /**
     * Silinmiş kitapları getirir.
     * Admin panelinde "çöp kutusu" görünümü için kullanılır.
     */
    List<Book> findByIsActiveFalse();

    /**
     * Gelişmiş kitap arama sorgusu.
     */
    @Query(value = "SELECT DISTINCT b.* FROM books b " +
            "LEFT JOIN authors a ON a.id = b.author_id " +
            "LEFT JOIN categories c ON c.id = b.category_id " +
            "LEFT JOIN publishers p ON p.id = b.publisher_id " +
            "LEFT JOIN shelves s ON s.id = b.shelf_id " +
            "LEFT JOIN libraries l ON l.id = s.library_id " +
            "WHERE b.is_active = true AND " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:keyword IS NULL OR LOWER(b.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:authorId IS NULL OR a.id = :authorId) AND " +
            "(:publisherId IS NULL OR p.id = :publisherId) AND " +
            "(:categoryId IS NULL OR c.id = :categoryId) AND " +
            "(:libraryId IS NULL OR l.id = :libraryId) AND " +
            "(:shelfId IS NULL OR s.id = :shelfId)" +
            "ORDER BY b.title ASC",
            nativeQuery = true)
    List<Book> filterBooks(
            @Param("title") String title,
            @Param("keyword") String keyword,
            @Param("authorId") Long authorId,
            @Param("publisherId") Long publisherId,
            @Param("categoryId") Long categoryId,
            @Param("libraryId") Long libraryId,
            @Param("shelfId") Long shelfId
    );

    /**
     * Basit metin araması — başlığa göre aktif kitaplar.
     */
    List<Book> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);

    /**
     * En çok kiralanan kitapları getirir — istatistik için.
     */
    @Query("SELECT bc.book.title, COUNT(r) as rentalCount " +
            "FROM Rental r JOIN r.bookCopy bc " +
            "GROUP BY bc.book.title " +
            "ORDER BY rentalCount DESC")
    List<Object[]> findMostRentedBooks();
}