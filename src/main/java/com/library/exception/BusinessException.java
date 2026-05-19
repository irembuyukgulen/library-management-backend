package com.library.exception;

/**
 * İş kuralı ihlallerinde fırlatılan exception.
 * GlobalExceptionHandler tarafından yakalanır ve 400 Bad Request döner.
 * ResourceNotFoundException'dan farkı:
 * - ResourceNotFoundException → kayıt yok (404)
 * - BusinessException → kayıt var ama iş kuralı engelledi (400)
 * Kullanım örnekleri:
 * - Kopya müsait değil (kirada veya hasarlı)
 * - Kullanıcı yasaklı
 * - Email zaten kayıtlı
 * - Bu kitap için zaten aktif rezervasyon var
 * - Kiralama zaten tamamlanmış
 */
public class BusinessException extends RuntimeException {

    /**
     * Kullanıcıya gösterilecek hata mesajı
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Kullanıcıya gösterilecek hata mesajı ve asıl hata sebebi
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}