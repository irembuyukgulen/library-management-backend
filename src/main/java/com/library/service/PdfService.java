package com.library.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.library.dto.InventoryReportItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDF rapor oluşturma işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfService {

    private final ReportService reportService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final BaseColor HEADER_COLOR = new BaseColor(41, 128, 185);
    private static final BaseColor SUMMARY_COLOR = new BaseColor(214, 234, 248);
    private static final BaseColor OVERDUE_COLOR = new BaseColor(255, 235, 235);
    private static final BaseColor RENTED_COLOR = BaseColor.WHITE;
    private static final float[] COLUMN_WIDTHS = {3f, 1.5f, 1.5f, 1.5f, 1f, 1f, 2f};

    /**
     * Envanter raporunu PDF formatında oluşturur.
     */
    public byte[] exportInventoryToPdf() throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                Phrase footer = new Phrase("Sayfa " + writer.getPageNumber(),
                        new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY));
                ColumnText.showTextAligned(cb,
                        Element.ALIGN_CENTER, footer,
                        (document.right() - document.left()) / 2 + document.leftMargin(),
                        document.bottom() - 10, 0);
            }
        });

        document.open();
        Font titleFont   = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
        Font headerFont  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Font cellFont    = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
        Font smallFont   = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);

        Paragraph title = new Paragraph("Kütüphane Envanter Raporu", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        Paragraph subtitle = new Paragraph("Rapor Tarihi: " +
                LocalDate.now().format(DATE_FORMATTER), subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);


        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(COLUMN_WIDTHS);
        table.setSpacingBefore(5f);

        addTableHeader(table, headerFont);

        List<InventoryReportItem> items = reportService.getInventoryReport();

        for (InventoryReportItem item : items) {
            addTableRow(table, item, cellFont);
        }

        document.add(table);

        Paragraph footer = new Paragraph("\nToplam " + items.size() + " kayıt listelendi.", smallFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        Paragraph legend = new Paragraph("🔵 Kütüphanede (müsait)   ⚪ Kirada   🔴 Gecikmiş", smallFont);
        legend.setAlignment(Element.ALIGN_LEFT);
        legend.setSpacingBefore(5);
        document.add(legend);

        document.close();

        log.info("PDF rapor oluşturuldu: {} kayıt", items.size());

        return outputStream.toByteArray();
    }

    private void addTableHeader(PdfPTable table, Font headerFont) {
        String[] headers = {
                "Kitap Adı", "Yazar", "Kategori",
                "Kopya Kodu", "Durum", "Müsait Adet", "İade Tarihi"
        };

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            cell.setBorderColor(BaseColor.WHITE);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, InventoryReportItem item, Font cellFont) {
        BaseColor rowColor;
        if ("SUMMARY".equals(item.getRowType())) {
            rowColor = SUMMARY_COLOR;
        } else if (Boolean.TRUE.equals(item.getIsOverdue())) {
            rowColor = OVERDUE_COLOR;
        } else {
            rowColor = RENTED_COLOR;
        }

        String statusText;

        if ("SUMMARY".equals(item.getRowType())) {
            statusText = "Kütüphanede";
        } else if (Boolean.TRUE.equals(item.getIsOverdue())) {
            statusText = "⚠ Gecikmiş";
        } else {
            statusText = "Kirada";
        }

        String availableText = "SUMMARY".equals(item.getRowType()) ? String.valueOf(item.getAvailableCopies()) : "—";
        String dueDateText = item.getDueDate() != null ? item.getDueDate().format(DATE_FORMATTER) : "—";

        addCell(table, item.getBookTitle(), rowColor, cellFont, Element.ALIGN_LEFT);
        addCell(table, item.getAuthorName(), rowColor, cellFont, Element.ALIGN_LEFT);
        addCell(table, item.getCategoryName(), rowColor, cellFont, Element.ALIGN_LEFT);
        addCell(table, item.getCopyCode() != null ? item.getCopyCode() : "—",
                rowColor, cellFont, Element.ALIGN_CENTER);
        addCell(table, statusText, rowColor, cellFont, Element.ALIGN_CENTER);
        addCell(table, availableText, rowColor, cellFont, Element.ALIGN_CENTER);
        addCell(table, dueDateText, rowColor, cellFont, Element.ALIGN_CENTER);
    }


    private void addCell(PdfPTable table, String value, BaseColor bgColor, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }
}