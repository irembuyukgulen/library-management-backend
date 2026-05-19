package com.library.repository;

import com.library.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kütüphane veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

    /**
     * İsme göre tam eşleşme ile kütüphane arar — büyük/küçük harf duyarsız.
     */
    Optional<Library> findByNameIgnoreCase(String name);
}