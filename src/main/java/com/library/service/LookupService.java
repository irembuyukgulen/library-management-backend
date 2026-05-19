package com.library.service;

import com.library.entity.*;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tanım/Lookup tablolarını yöneten servis.
 * Yazar, Yayınevi, Kategori, Kütüphane, Raf CRUD işlemleri.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LookupService {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final LibraryRepository libraryRepository;
    private final ShelfRepository shelfRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional
    public Author saveAuthor(Author author) {
        Author saved = authorRepository.save(author);

        log.info("Yazar kaydedildi: {}", saved.getName());

        return saved;
    }

    @Transactional
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Yazar bulunamadı: ID=" + id);
        }

        authorRepository.deleteById(id);

        log.info("Yazar silindi: ID={}", id);
    }

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    @Transactional
    public Publisher savePublisher(Publisher publisher) {
        Publisher saved = publisherRepository.save(publisher);

        log.info("Yayınevi kaydedildi: {}", saved.getName());

        return saved;
    }

    @Transactional
    public void deletePublisher(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Yayınevi bulunamadı: ID=" + id);
        }

        publisherRepository.deleteById(id);

        log.info("Yayınevi silindi: ID={}", id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category saveCategory(Category category) {
        Category saved = categoryRepository.save(category);

        log.info("Kategori kaydedildi: {}", saved.getName());

        return saved;
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kategori bulunamadı: ID=" + id);
        }

        categoryRepository.deleteById(id);

        log.info("Kategori silindi: ID={}", id);
    }

    public List<Library> getAllLibraries() {
        return libraryRepository.findAll();
    }

    @Transactional
    public Library saveLibrary(Library library) {
        Library saved = libraryRepository.save(library);

        log.info("Kütüphane kaydedildi: {}", saved.getName());

        return saved;
    }

    @Transactional
    public void deleteLibrary(Long id) {
        if (!libraryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kütüphane bulunamadı: ID=" + id);
        }

        libraryRepository.deleteById(id);

        log.info("Kütüphane silindi: ID={}", id);
    }

    public List<Shelf> getAllShelves() {
        return shelfRepository.findAll();
    }

    public List<Shelf> getShelvesByLibrary(Long libraryId) {
        return shelfRepository.findByLibraryId(libraryId);
    }

    @Transactional
    public Shelf saveShelf(Shelf shelf) {
        Shelf saved = shelfRepository.save(shelf);

        log.info("Raf kaydedildi: {}", saved.getShelfNumber());

        return saved;
    }

    @Transactional
    public void deleteShelf(Long id) {
        if (!shelfRepository.existsById(id)) {
            throw new ResourceNotFoundException("Raf bulunamadı: ID=" + id);
        }

        shelfRepository.deleteById(id);

        log.info("Raf silindi: ID={}", id);
    }
}