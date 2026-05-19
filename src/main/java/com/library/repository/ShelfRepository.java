package com.library.repository;

import com.library.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Raf veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    /**
     * Belirli bir kütüphanenin tüm raflarını getirir.
     */
    List<Shelf> findByLibraryId(Long libraryId);

    /**
     * Raf numarası ve kütüphane ID'sine göre raf arar.
     */
    Optional<Shelf> findByShelfNumberIgnoreCaseAndLibraryId(String shelfNumber, Long libraryId);
}