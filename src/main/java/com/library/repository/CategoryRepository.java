package com.library.repository;

import com.library.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Kategori veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * İsme göre kategori arar — büyük/küçük harf duyarsız, kısmi eşleşme.
     */
    List<Category> findByNameContainingIgnoreCase(String name);

    /**
     * İsme göre tam eşleşme ile kategori arar.
     */
    Optional<Category> findByNameIgnoreCase(String name);
}