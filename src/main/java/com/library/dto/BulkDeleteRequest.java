package com.library.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Toplu silme isteği için DTO.
 * Admin birden fazla kitabı seçip tek istekle silebilir.
 * Soft delete uygulanır — kitaplar isActive=false olur.
 */
@Data
public class BulkDeleteRequest {

    /**
     * Silinecek kitapların ID listesi.
     * @NotEmpty → boş liste gönderilemez, en az 1 ID olmalı
     */
    @NotEmpty(message = "En az bir kitap ID'si girilmelidir")
    private List<Long> ids;
}