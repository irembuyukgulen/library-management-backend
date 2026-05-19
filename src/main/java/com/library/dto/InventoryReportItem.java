package com.library.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * Envanter raporu için DTO.
 * Her satır ya bir kitabın kütüphanedeki stok özetini
 * ya da kiradaki tek bir kopyasını temsil eder.
 * rowType değerleri:
 * "SUMMARY" → Kütüphanedeki kopyaların özeti (grupla topla)
 * "RENTED"  → Kiradaki tek bir kopya detayı
 */
@Data
public class InventoryReportItem {

    /** Kitabın ID'si */
    private Long bookId;

    /** Kitabın başlığı */
    private String bookTitle;

    /** Yazarın adı */
    private String authorName;

    /** Kategorinin adı */
    private String categoryName;

    /**
     * Müsait kopya sayısı.
     * SUMMARY satırlarında dolu, RENTED satırlarında 0.
     */
    private int availableCopies;

    /** Kopyanın ID'si — RENTED satırlarında dolu */
    private Long copyId;

    /** Kopya kodu — RENTED satırlarında dolu */
    private String copyCode;

    /** Kitabı kiralayan kullanıcının adı — RENTED satırlarında dolu */
    private String rentedByUser;

    /**
     * İade tarihi — RENTED satırlarında dolu.
     * Frontend'de gecikmiş olanları kırmızı gösterir.
     */
    private LocalDate dueDate;

    /**
     * Gecikmiş mi?
     * true → dueDate bugünden önce ve henüz iade edilmemiş
     */
    private Boolean isOverdue;

    /**
     * Satır türü: "SUMMARY" veya "RENTED"
     * Frontend bu değere göre farklı stil uygular.
     */
    private String rowType;
}