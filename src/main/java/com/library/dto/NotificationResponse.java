// ─── NotificationResponse.java ───────────────────────────────────────────────
package com.library.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * Kira süresi bildirimi için DTO.
 * Kullanıcı giriş yaptığında frontend bu endpoint'i çağırır.
 * Süresi yaklaşan veya geçmiş kiralamalar bildirim olarak döner.
 * type değerleri:
 * "WARNING" → iade tarihine 3 gün veya daha az kaldı
 * "OVERDUE" → iade tarihi geçmiş
 */
@Data
public class NotificationResponse {

    /** Kiralama ID'si — detaya gitmek için */
    private Long rentalId;

    /** Kitabın başlığı */
    private String bookTitle;

    /** İade edilmesi gereken tarih */
    private LocalDate dueDate;

    /**
     * Kalan gün sayısı.
     * Pozitif → bu kadar gün kaldı
     * Negatif → bu kadar gün gecikmiş
     */
    private Long daysRemaining;

    /** Bildirim türü: "WARNING" veya "OVERDUE" */
    private String type;

    /** Kullanıcıya gösterilecek mesaj */
    private String message;
}