package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Kütüphane raflarını temsil eder.
 */

@Data
@Entity
@Table(name = "shelves")
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Bu rafın ait olduğu kütüphane.
     */
    @ManyToOne
    @JoinColumn(name = "library_id")
    private Library library;

    /** Raf numarası */
    @Column(nullable = false)
    private String shelfNumber;

    /** Raf bölmesi */
    private String section;

    /** Kaydın oluşturulma tarihi */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}