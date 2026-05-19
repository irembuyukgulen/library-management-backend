package com.library.service;

import com.library.entity.*;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * "Bul veya Oluştur" (Find or Create) mantığını yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FindOrCreateService {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final LibraryRepository libraryRepository;
    private final ShelfRepository shelfRepository;

    /**
     * İsme göre yazar bulur veya yeni yazar oluşturur.
     */
    @Transactional
    public Author findOrCreateAuthor(String name) {
        if (!StringUtils.hasText(name)) return null;

        String trimmedName = name.trim();

        return authorRepository.findByNameIgnoreCase(trimmedName)
                .orElseGet(() -> {
                    // Bulunamadı — yeni yazar oluştur
                    log.info("Yeni yazar oluşturuluyor: {}", trimmedName);
                    Author author = new Author();
                    author.setName(trimmedName);
                    return authorRepository.save(author);
                });
    }

    /**
     * İsme göre yayınevi bulur veya yeni yayınevi oluşturur.
     */
    @Transactional
    public Publisher findOrCreatePublisher(String name) {
        if (!StringUtils.hasText(name)) return null;

        String trimmedName = name.trim();

        return publisherRepository.findByNameIgnoreCase(trimmedName)
                .orElseGet(() -> {
                    log.info("Yeni yayınevi oluşturuluyor: {}", trimmedName);
                    Publisher publisher = new Publisher();
                    publisher.setName(trimmedName);
                    return publisherRepository.save(publisher);
                });
    }

    /**
     * İsme göre kategori bulur veya yeni kategori oluşturur.
     */
    @Transactional
    public Category findOrCreateCategory(String name) {
        if (!StringUtils.hasText(name)) return null;

        String trimmedName = name.trim();

        return categoryRepository.findByNameIgnoreCase(trimmedName)
                .orElseGet(() -> {
                    log.info("Yeni kategori oluşturuluyor: {}", trimmedName);
                    Category category = new Category();
                    category.setName(trimmedName);
                    return categoryRepository.save(category);
                });
    }

    /**
     * İsme göre kütüphane bulur veya yeni kütüphane oluşturur.
     */
    @Transactional
    public Library findOrCreateLibrary(String name, String address, String phone) {
        if (!StringUtils.hasText(name)) return null;

        String trimmedName = name.trim();

        return libraryRepository.findByNameIgnoreCase(trimmedName)
                .orElseGet(() -> {
                    log.info("Yeni kütüphane oluşturuluyor: {}", trimmedName);
                    Library library = new Library();
                    library.setName(trimmedName);
                    library.setAddress(StringUtils.hasText(address) ? address.trim() : "");
                    library.setPhone(StringUtils.hasText(phone) ? phone.trim() : "");
                    return libraryRepository.save(library);
                });
    }

    /**
     * Raf numarası ve kütüphaneye göre raf bulur veya yeni raf oluşturur.
     */
    @Transactional
    public Shelf findOrCreateShelf(String shelfNumber, String section, Library library) {
        if (!StringUtils.hasText(shelfNumber)) return null;

        String trimmedNumber = shelfNumber.trim();

        if (library != null) {
            return shelfRepository
                    .findByShelfNumberIgnoreCaseAndLibraryId(trimmedNumber, library.getId())
                    .orElseGet(() -> createShelf(trimmedNumber, section, library));
        }

        return shelfRepository.findAll().stream()
                .filter(s -> s.getShelfNumber().equalsIgnoreCase(trimmedNumber))
                .findFirst()
                .orElseGet(() -> createShelf(trimmedNumber, section, null));
    }

    /**
     * Yeni raf oluşturan yardımcı metod.
     */
    private Shelf createShelf(String shelfNumber, String section, Library library) {
        log.info("Yeni raf oluşturuluyor: {} ({})", shelfNumber,
                library != null ? library.getName() : "kütüphane yok");
        Shelf shelf = new Shelf();
        shelf.setShelfNumber(shelfNumber);
        shelf.setSection(StringUtils.hasText(section) ? section.trim() : "");
        shelf.setLibrary(library);
        return shelfRepository.save(shelf);
    }
}