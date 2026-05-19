package com.library.controller;

import com.library.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * PDF rapor endpoint'lerini sunan Controller.
 * Tüm endpoint'ler ADMIN'e özel.
 * Endpoint'ler:
 * GET /api/admin/reports/inventory/pdf → Envanter raporunu PDF indir
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    /**
     * Envanter raporunu PDF formatında indirir.
     */
    @GetMapping("/reports/inventory/pdf")
    public ResponseEntity<byte[]> exportInventoryPdf() throws Exception {
        byte[] data = pdfService.exportInventoryToPdf();

        String filename = "envanter_" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}