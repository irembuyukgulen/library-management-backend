package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kütüphane şubelerini temsil eder.
 */

@Data
@Entity
@Table(name = "libraries")
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Kütüphanenin adı */
    @Column(nullable = false)
    private String name;

    /** Fiziksel adresi */
    private String address;

    /** İletişim telefonu */
    private String phone;

    /**
     * Bu kütüphaneye ait raflar.
     */
    @OneToMany(mappedBy = "library")
    private List<Shelf> shelves;

    /** Kaydın oluşturulma tarihi */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}