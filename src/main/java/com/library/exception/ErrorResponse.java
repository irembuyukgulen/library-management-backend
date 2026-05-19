package com.library.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API hata yanıtlarının standart formatı.
 * hata mesajlarını kolayca gösterebilir.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    /** HTTP durum kodu */
    private int status;

    /** Kullanıcıya gösterilecek hata mesajı */
    private String message;

    /**
     * Hatanın oluştuğu zaman.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}