package com.library.repository;

import com.library.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Yayınevi veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    /**
     * İsme göre yayınevi arar — büyük/küçük harf duyarsız, kısmi eşleşme.
     */
    List<Publisher> findByNameContainingIgnoreCase(String name);

    /**
     * İsme göre tam eşleşme ile yayınevi arar.
     */
    Optional<Publisher> findByNameIgnoreCase(String name);
}