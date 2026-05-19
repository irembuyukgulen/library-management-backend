package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Kitap müsait olduğunda bildirilmek isteyen kullanıcıları tutan Entity.
 */
@Data
@Entity
@Table(name = "waitlist_notifications", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"book_id", "user_id"})
})
public class WaitlistNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Bildirimin ilgili olduğu kitap.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Bildirilmek isteyen kullanıcı.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean notified = false;

    /**
     * Bildirimin gönderildiği tarih ve saat.
     */
    private LocalDateTime notifiedAt;

    /**
     * Kaydın oluşturulduğu tarih ve saat.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Kayıt ilk kez veritabanına yazılmadan önce otomatik çalışır.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}