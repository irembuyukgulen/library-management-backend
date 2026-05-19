package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Sistem genelindeki tüm önemli işlemleri loglar.
 * "Kim, ne zaman, ne yaptı?" sorusunu yanıtlar.
 * Kurumsal sistemlerde audit log kritiktir:
 * - Güvenlik ihlallerini tespit etmeyi sağlar.
 * - Hata durumlarında geriye dönük inceleme yapılabilir.
 * - Yasal uyumluluk gereklilikleri karşılanır.
 */

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * İşlemi yapan kullanıcının e-postası.
     * "system" değeri sistem tarafından otomatik yapılan işlemleri gösterir.
     */
    private String userEmail;

    /**
     * Yapılan işlemin tipi.
     * Büyük harf, alt çizgi ile ayrılmış format kullanılır.
     */
    private String action;

    /**
     * İşlemin yapıldığı entity türü.
     */
    private String entityType;

    /**
     * İşlemin yapıldığı kaydın ID'si.
     * entityType ile birlikte hangi kayıt üzerinde işlem yapıldığını belirtir.
     */
    private Long entityId;

    /**
     * İşlem hakkında ek detaylar.
     * TEXT tipi → uzun açıklamalar için.
     */
    @Column(columnDefinition = "TEXT")
    private String details;

    /** İşlemin gerçekleştiği tarih ve saat */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}