package com.library.service;

import com.library.entity.*;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel import/export işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final FindOrCreateService findOrCreateService;
    private static final int COL_ID          = 0;
    private static final int COL_TITLE       = 1;
    private static final int COL_ISBN        = 2;
    private static final int COL_AUTHOR      = 3;
    private static final int COL_PUBLISHER   = 4;
    private static final int COL_CATEGORY    = 5;
    private static final int COL_SHELF       = 6;
    private static final int COL_DESCRIPTION = 7;
    private static final int COL_KEYWORDS    = 8;
    private static final int COL_TOTAL_COPY  = 9;
    private static final int COL_AVAIL_COPY  = 10;
    private static final int COL_STATUS      = 11;
    private static final int IMP_TITLE       = 0;
    private static final int IMP_ISBN        = 1;
    private static final int IMP_AUTHOR      = 2;
    private static final int IMP_PUBLISHER   = 3;
    private static final int IMP_CATEGORY    = 4;
    private static final int IMP_DESCRIPTION = 5;
    private static final int IMP_KEYWORDS    = 6;

    /**
     * Tüm aktif kitapları Excel dosyasına aktarır.
     */
    public byte[] exportBooksToExcel() throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Kitaplar");

            CellStyle headerStyle = createHeaderStyle(workbook);

            writeHeaderRow(sheet, headerStyle);

            List<Book> books = bookRepository.findByIsActiveTrue();
            CellStyle evenRowStyle = createEvenRowStyle(workbook);

            for (int i = 0; i < books.size(); i++) {
                Row row = sheet.createRow(i + 1);
                if (i % 2 == 0) {
                    applyStyleToRow(row, evenRowStyle, 12);
                }
                writeBookRow(row, books.get(i));
            }

            for (int i = 0; i <= COL_STATUS; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            log.info("Excel export tamamlandı: {} kitap", books.size());
            return outputStream.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createEvenRowStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void applyStyleToRow(Row row, CellStyle style, int colCount) {
        for (int i = 0; i < colCount; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
        }
    }

    private void writeHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "ID", "Başlık", "ISBN", "Yazar", "Yayınevi",
                "Kategori", "Raf", "Açıklama", "Anahtar Kelimeler",
                "Toplam Kopya", "Müsait Kopya", "Durum"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        headerRow.setHeightInPoints(20);
    }

    private void writeBookRow(Row row, Book book) {
        createCell(row, COL_ID, book.getId() != null ? book.getId().toString() : "");
        createCell(row, COL_TITLE, book.getTitle());
        createCell(row, COL_ISBN, book.getIsbn() != null ? book.getIsbn() : "");
        createCell(row, COL_AUTHOR,
                book.getAuthor() != null ? book.getAuthor().getName() : "");
        createCell(row, COL_PUBLISHER,
                book.getPublisher() != null ? book.getPublisher().getName() : "");
        createCell(row, COL_CATEGORY,
                book.getCategory() != null ? book.getCategory().getName() : "");
        createCell(row, COL_SHELF,
                book.getShelf() != null ? book.getShelf().getShelfNumber() : "");
        createCell(row, COL_DESCRIPTION,
                book.getDescription() != null ? book.getDescription() : "");
        createCell(row, COL_KEYWORDS,
                book.getKeywords() != null ? book.getKeywords() : "");
        List<BookCopy> copies = bookCopyRepository.findByBookId(book.getId());
        long availableCount = copies.stream()
                .filter(c -> c.getStatus() == BookCopy.CopyStatus.AVAILABLE)
                .count();
        createCell(row, COL_TOTAL_COPY, String.valueOf(copies.size()));
        createCell(row, COL_AVAIL_COPY, String.valueOf(availableCount));
        createCell(row, COL_STATUS, availableCount > 0 ? "Müsait" : "Müsait Değil");
    }

    private void createCell(Row row, int colIndex, String value) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value != null ? value : "");
    }

    /**
     * Excel dosyasından kitapları toplu olarak içe aktarır.
     */
    @Transactional
    public List<String> importBooksFromExcel(MultipartFile file) throws IOException {

        List<String> results = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            int successCount = 0;
            int skipCount = 0;
            int errorCount = 0;

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String result = processImportRow(row, i + 1);
                    results.add(result);

                    if (result.contains("başarıyla")) {
                        successCount++;
                    } else {
                        skipCount++;
                    }
                } catch (Exception e) {
                    String errorMsg = "Satır " + (i + 1) + ": Hata — " + e.getMessage();
                    results.add(errorMsg);
                    errorCount++;
                    log.warn("Excel import satır hatası: {}", errorMsg);
                }
            }

            results.add(0, String.format(
                    "📊 Import özeti: %d başarılı, %d atlandı, %d hata",
                    successCount, skipCount, errorCount));

            log.info("Excel import tamamlandı: {} başarılı, {} atlandı, {} hata",
                    successCount, skipCount, errorCount);
        }

        return results;
    }

    private String processImportRow(Row row, int rowNumber) {
        String title = getCellValue(row, IMP_TITLE);

        if (title.isEmpty()) {
            return "Satır " + rowNumber + ": Başlık boş — atlandı";
        }

        String isbn        = getCellValue(row, IMP_ISBN);
        String authorName  = getCellValue(row, IMP_AUTHOR);
        String publisherName = getCellValue(row, IMP_PUBLISHER);
        String categoryName  = getCellValue(row, IMP_CATEGORY);
        String description   = getCellValue(row, IMP_DESCRIPTION);
        String keywords      = getCellValue(row, IMP_KEYWORDS);

        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn.isEmpty() ? null : isbn);
        book.setDescription(description.isEmpty() ? null : description);
        book.setKeywords(keywords.isEmpty() ? null : keywords);
        book.setIsActive(true);

        book.setAuthor(findOrCreateService.findOrCreateAuthor(authorName));
        book.setPublisher(findOrCreateService.findOrCreatePublisher(publisherName));
        book.setCategory(findOrCreateService.findOrCreateCategory(categoryName));

        bookRepository.save(book);

        return "Satır " + rowNumber + ": \"" + title + "\" başarıyla eklendi";
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    yield String.valueOf((long) numericValue);
                }
                yield String.valueOf(numericValue);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue().trim();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }
}