package com.library.exception;

/**
 * İstenen kayıt veritabanında bulunamadığında fırlatılan exception.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Kullanıcıya gösterilecek hata mesajı
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Hata mesajı ve sebebiyle birlikte exception oluşturur.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}