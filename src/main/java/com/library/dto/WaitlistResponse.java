package com.library.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Bekleme listesi kaydını frontend'e taşıyan DTO.
 * Kullanım yerleri:
 * - Üye paneli: "Bekleme listem" ekranında kullanıcının beklediği kitaplar
 * - Admin paneli: Hangi kitap için kaç kişi bekliyor görünümü
 * Entity'den farkı:
 * - user bilgisi yok — response zaten o kullanıcıya özel döner
 * - Book entity'sinin tamamı değil, sadece id/title/thumbnail gelir
 */
@Data
public class WaitlistResponse {

    /** Bekleme kaydının ID'si — iptal işlemi için kullanılır */
    private Long id;

    /** Beklenen kitabın ID'si */
    private Long bookId;

    /** Beklenen kitabın başlığı — listede göstermek için */
    private String bookTitle;

    /**
     * Kitap kapak görseli URL'i.
     * null olabilir — entity'de thumbnail zorunlu alan değil.
     */
    private String bookThumbnail;

    /**
     * Bildirim gönderildi mi?
     * false → kitap henüz müsait olmadı veya bildirim gönderilmedi
     * true  → kullanıcıya "kitap müsait" bildirimi gönderildi
     */
    private Boolean notified;

    /**
     * Bildirimin gönderildiği tarih.
     * notified = false iken null gelir.
     */
    private LocalDateTime notifiedAt;

    /**
     * Bekleme listesine eklenme tarihi.
     */
    private LocalDateTime createdAt;
}