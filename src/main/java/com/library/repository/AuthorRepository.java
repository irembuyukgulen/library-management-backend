package com.library.repository;

import com.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Yazar veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * İsme göre yazar arar — büyük/küçük harf duyarsız, kısmi eşleşme.
     */
    List<Author> findByNameContainingIgnoreCase(String name);

    /**
     * İsme göre tam eşleşme ile yazar arar — büyük/küçük harf duyarsız.
     */
    Optional<Author> findByNameIgnoreCase(String name);
}