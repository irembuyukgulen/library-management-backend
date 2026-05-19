package com.library.config;

import com.library.entity.*;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Uygulama ilk başladığında otomatik çalışan veri yükleme sınıfı.
 * CommandLineRunner → Spring Boot uygulaması ayağa kalktıktan sonra
 * run() metodunu otomatik çalıştırır.
 * Çalışma koşulu:
 * Eğer veritabanında kullanıcı varsa hiçbir şey yapma.
 * Bu sayede her restart'ta tekrar veri eklenmez.
 * Eklenen veriler:
 * - 1 Admin + 1 Üye kullanıcı
 * - Sistem ayarları (kiralama süresi, ücretler)
 * - 4 Kategori, 4 Yazar, 3 Yayınevi
 * - 1 Kütüphane, 3 Raf
 * - 4 Kitap (toplam 9 kopya)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final LibraryRepository libraryRepository;
    private final ShelfRepository shelfRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final SystemSettingsRepository systemSettingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        // Zaten veri varsa hiçbir şey yapma
        if (userRepository.count() > 0) {
            log.info("Veritabanında veri mevcut, DataSeeder atlandı.");
            return;
        }

        log.info("🌱 DataSeeder başlatılıyor...");

        saveSystemSettings();
        User admin = saveUsers();
        Category[] categories = saveCategories();
        Author[] authors = saveAuthors();
        Publisher[] publishers = savePublishers();
        Library library = saveLibrary();
        Shelf[] shelves = saveShelves(library);
        saveBooks(categories, authors, publishers, shelves);

        log.info("✅ DataSeeder tamamlandı!");
        log.info("👤 Admin  → admin@library.com / admin123");
        log.info("👤 Üye    → irem@library.com  / member123");
    }


    private void saveSystemSettings() {
        // Standart ödünç alma süresi (gün)
        saveSetting("standard_loan_days", "14",
                "Kitabın iade edilmesi gereken standart gün sayısı");

        // Günlük kiralama ücreti (TL)
        saveSetting("daily_rental_fee", "2.50",
                "Kitap başına günlük kiralama ücreti (TL)");

        // Günlük gecikme cezası (TL)
        saveSetting("daily_late_fee", "5.00",
                "Geç iade için günlük ceza bedeli (TL)");

        // Rezervasyon geçerlilik süresi (gün)
        saveSetting("reservation_days", "7",
                "Rezervasyonun geçerli olduğu maksimum gün sayısı");

        log.info("  ✓ Sistem ayarları kaydedildi");
    }

    /**
     * Kullanıcıları kaydeder.
     * Şifreler BCrypt ile hashlenir — düz metin saklanmaz.
     */
    private User saveUsers() {
        // Admin kullanıcı
        User admin = new User();
        admin.setName("Admin");
        admin.setSurname("Kullanıcı");
        admin.setEmail("admin@library.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        admin.setIsBanned(false);
        userRepository.save(admin);

        // Örnek üye
        User member = new User();
        member.setName("İrem");
        member.setSurname("Üye");
        member.setEmail("irem@library.com");
        member.setPasswordHash(passwordEncoder.encode("member123"));
        member.setRole(User.Role.MEMBER);
        member.setIsBanned(false);
        userRepository.save(member);

        log.info("  ✓ Kullanıcılar kaydedildi (1 admin, 1 üye)");
        return admin;
    }

    /**
     * Kategorileri kaydeder.
     */
    private Category[] saveCategories() {
        Category roman = saveCategory("Roman");
        Category bilim = saveCategory("Bilim");
        Category tarih = saveCategory("Tarih");
        Category kisiselGelisim = saveCategory("Kişisel Gelişim");

        log.info("  ✓ Kategoriler kaydedildi (4 adet)");
        return new Category[]{roman, bilim, tarih, kisiselGelisim};
    }

    /**
     * Yazarları kaydeder.
     */
    private Author[] saveAuthors() {
        Author orwell = saveAuthor("George Orwell");
        Author tolkien = saveAuthor("J.R.R. Tolkien");
        Author hawking = saveAuthor("Stephen Hawking");
        Author covey = saveAuthor("Stephen Covey");

        log.info("  ✓ Yazarlar kaydedildi (4 adet)");
        return new Author[]{orwell, tolkien, hawking, covey};
    }

    /**
     * Yayınevlerini kaydeder.
     */
    private Publisher[] savePublishers() {
        Publisher yapiKredi = savePublisher("Yapı Kredi Yayınları");
        Publisher iletisim = savePublisher("İletişim Yayınları");
        Publisher metis = savePublisher("Metis Yayınları");

        log.info("  ✓ Yayınevleri kaydedildi (3 adet)");
        return new Publisher[]{yapiKredi, iletisim, metis};
    }

    /**
     * Kütüphaneyi kaydeder.
     */
    private Library saveLibrary() {
        Library library = new Library();
        library.setName("Merkez Kütüphane");
        library.setAddress("Ankara, Çankaya");
        library.setPhone("0312 000 0000");
        libraryRepository.save(library);

        log.info("  ✓ Kütüphane kaydedildi");
        return library;
    }

    /**
     * Rafları kaydeder.
     */
    private Shelf[] saveShelves(Library library) {
        Shelf shelf1 = saveShelf(library, "A1", "Roman Bölümü");
        Shelf shelf2 = saveShelf(library, "B1", "Bilim Bölümü");
        Shelf shelf3 = saveShelf(library, "C1", "Kişisel Gelişim Bölümü");

        log.info("  ✓ Raflar kaydedildi (3 adet)");
        return new Shelf[]{shelf1, shelf2, shelf3};
    }

    /**
     * Kitapları ve kopyalarını kaydeder.
     */
    private void saveBooks(Category[] categories, Author[] authors,
                           Publisher[] publishers, Shelf[] shelves) {

        Book book1 = saveBook(
                "1984",
                "978-975-08-0987-6",
                authors[0], publishers[0], categories[0], shelves[0],
                "Distopik bir gelecekte geçen klasik roman. " +
                        "Büyük Birader'in gözetim altındaki bir toplumu anlatır.",
                "distopya, siyaset, gelecek, gözetim",
                328
        );
        addCopies(book1, 3);

        Book book2 = saveBook(
                "Yüzüklerin Efendisi",
                "978-975-08-1234-5",
                authors[1], publishers[1], categories[0], shelves[0],
                "Orta Dünya'da geçen epik fantezi roman serisi.",
                "fantezi, macera, orta dünya, elfler",
                1178
        );
        addCopies(book2, 2);

        Book book3 = saveBook(
                "Zamanın Kısa Tarihi",
                "978-975-08-2345-6",
                authors[2], publishers[2], categories[1], shelves[1],
                "Evrenin başlangıcı, kara delikler ve zamanın doğası üzerine.",
                "evren, fizik, kozmoloji, kara delik",
                212
        );
        addCopies(book3, 2);

        Book book4 = saveBook(
                "Etkili İnsanların 7 Alışkanlığı",
                "978-975-08-3456-7",
                authors[3], publishers[0], categories[3], shelves[2],
                "Kişisel ve profesyonel gelişim için 7 temel alışkanlık.",
                "liderlik, başarı, alışkanlık, verimlilik",
                372
        );
        addCopies(book4, 2);

        log.info("  ✓ Kitaplar kaydedildi (4 kitap, 9 kopya)");
    }

    /** Sistem ayarı kaydeder — zaten varsa eklemez */
    private void saveSetting(String key, String value, String description) {
        if (systemSettingsRepository.findBySettingKey(key).isEmpty()) {
            SystemSettings setting = new SystemSettings();
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            setting.setDescription(description);
            systemSettingsRepository.save(setting);
        }
    }

    /** Kategori kaydeder */
    private Category saveCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    /** Yazar kaydeder */
    private Author saveAuthor(String name) {
        Author author = new Author();
        author.setName(name);
        return authorRepository.save(author);
    }

    /** Yayınevi kaydeder */
    private Publisher savePublisher(String name) {
        Publisher publisher = new Publisher();
        publisher.setName(name);
        return publisherRepository.save(publisher);
    }

    /** Raf kaydeder */
    private Shelf saveShelf(Library library, String number, String section) {
        Shelf shelf = new Shelf();
        shelf.setLibrary(library);
        shelf.setShelfNumber(number);
        shelf.setSection(section);
        return shelfRepository.save(shelf);
    }

    /** Kitap kaydeder */
    private Book saveBook(String title, String isbn, Author author,
                          Publisher publisher, Category category, Shelf shelf,
                          String description, String keywords, Integer pageCount) {
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategory(category);
        book.setShelf(shelf);
        book.setDescription(description);
        book.setKeywords(keywords);
        book.setPageCount(pageCount);
        book.setIsActive(true);
        return bookRepository.save(book);
    }

    /**
     * Kitaba belirtilen sayıda kopya ekler.
     * Kopya kodu: ISBN-1, ISBN-2 formatında.
     */
    private void addCopies(Book book, int count) {
        for (int i = 1; i <= count; i++) {
            BookCopy copy = new BookCopy();
            copy.setBook(book);
            // ISBN yoksa kitap ID'si kullan
            String prefix = book.getIsbn() != null ? book.getIsbn() : "BOOK-" + book.getId();
            copy.setCopyCode(prefix + "-" + i);
            copy.setStatus(BookCopy.CopyStatus.AVAILABLE);
            bookCopyRepository.save(copy);
        }
    }
}