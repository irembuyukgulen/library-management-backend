package com.library.service;

import com.library.dto.InventoryReportItem;
import com.library.entity.*;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Envanter raporu oluşturan servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final RentalRepository rentalRepository;

    /**
     * Envanter raporunu oluşturur.
     */
    public List<InventoryReportItem> getInventoryReport() {
        List<InventoryReportItem> report = new ArrayList<>();
        List<Book> books = bookRepository.findByIsActiveTrue();

        for (Book book : books) {
            int availableCount = bookCopyRepository
                    .countByBookIdAndStatus(book.getId(), BookCopy.CopyStatus.AVAILABLE);

            if (availableCount > 0) {
                InventoryReportItem summaryRow = buildSummaryRow(book, availableCount);
                report.add(summaryRow);
            }

            List<BookCopy> rentedCopies = bookCopyRepository
                    .findByBookIdAndStatus(book.getId(), BookCopy.CopyStatus.RENTED);

            for (BookCopy copy : rentedCopies) {
                InventoryReportItem rentedRow = buildRentedRow(book, copy);
                report.add(rentedRow);
            }
        }

        log.debug("Envanter raporu oluşturuldu: {} satır", report.size());

        return report;
    }

    private InventoryReportItem buildSummaryRow(Book book, int availableCount) {
        InventoryReportItem item = new InventoryReportItem();

        item.setRowType("SUMMARY");
        item.setBookId(book.getId());
        item.setBookTitle(book.getTitle());
        item.setAuthorName(book.getAuthor() != null ? book.getAuthor().getName() : "");
        item.setCategoryName(book.getCategory() != null ? book.getCategory().getName() : "");
        item.setAvailableCopies(availableCount);
        item.setIsOverdue(false);

        return item;
    }

    private InventoryReportItem buildRentedRow(Book book, BookCopy copy) {
        InventoryReportItem item = new InventoryReportItem();

        item.setRowType("RENTED");
        item.setBookId(book.getId());
        item.setBookTitle(book.getTitle());
        item.setAuthorName(book.getAuthor() != null ? book.getAuthor().getName() : "");
        item.setCategoryName(book.getCategory() != null ? book.getCategory().getName() : "");
        item.setCopyId(copy.getId());
        item.setCopyCode(copy.getCopyCode());
        item.setAvailableCopies(0);

        rentalRepository.findActiveRentalsWithDetails().stream()
                .filter(r -> r.getBookCopy().getId().equals(copy.getId()))
                .findFirst()
                .ifPresent(rental -> {
                    item.setRentedByUser(rental.getUser().getName() + " " +
                                    rental.getUser().getSurname());
                    item.setDueDate(rental.getDueDate());
                    item.setIsOverdue(rental.getDueDate().isBefore(LocalDate.now()));
                });

        return item;
    }
}