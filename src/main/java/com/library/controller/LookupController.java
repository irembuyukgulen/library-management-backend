package com.library.controller;

import com.library.entity.*;
import com.library.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tanım/Lookup tablolarını yöneten Controller.
 * Yazar, Yayınevi, Kategori, Kütüphane, Raf CRUD işlemleri.
 * Yetkilendirme:
 * GET  /api/{entity}          → Giriş yapılmış herkes (kitap listeleme için)
 * /**  /api/admin/{entity}    → Sadece ADMIN
 * Endpoint'ler:
 * GET    /api/authors            → Tüm yazarlar
 * POST   /api/admin/authors      → Yazar ekle (ADMIN)
 * PUT    /api/admin/authors/{id} → Yazar güncelle (ADMIN)
 * DELETE /api/admin/authors/{id} → Yazar sil (ADMIN)
 * (Aynı yapı publishers, categories, libraries, shelves için)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(lookupService.getAllAuthors());
    }

    @PostMapping("/admin/authors")
    public ResponseEntity<Author> saveAuthor(@RequestBody Author author) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.saveAuthor(author));
    }

    @PutMapping("/admin/authors/{id}")
    public ResponseEntity<Author> updateAuthor(
            @PathVariable Long id, @RequestBody Author author) {
        author.setId(id);
        return ResponseEntity.ok(lookupService.saveAuthor(author));
    }

    @DeleteMapping("/admin/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        lookupService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/publishers")
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(lookupService.getAllPublishers());
    }

    @PostMapping("/admin/publishers")
    public ResponseEntity<Publisher> savePublisher(@RequestBody Publisher publisher) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.savePublisher(publisher));
    }

    @PutMapping("/admin/publishers/{id}")
    public ResponseEntity<Publisher> updatePublisher(
            @PathVariable Long id, @RequestBody Publisher publisher) {
        publisher.setId(id);
        return ResponseEntity.ok(lookupService.savePublisher(publisher));
    }

    @DeleteMapping("/admin/publishers/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        lookupService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(lookupService.getAllCategories());
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<Category> saveCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.saveCategory(category));
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return ResponseEntity.ok(lookupService.saveCategory(category));
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        lookupService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/libraries")
    public ResponseEntity<List<Library>> getAllLibraries() {
        return ResponseEntity.ok(lookupService.getAllLibraries());
    }

    @PostMapping("/admin/libraries")
    public ResponseEntity<Library> saveLibrary(@RequestBody Library library) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.saveLibrary(library));
    }

    @PutMapping("/admin/libraries/{id}")
    public ResponseEntity<Library> updateLibrary(
            @PathVariable Long id, @RequestBody Library library) {
        library.setId(id);
        return ResponseEntity.ok(lookupService.saveLibrary(library));
    }

    @DeleteMapping("/admin/libraries/{id}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable Long id) {
        lookupService.deleteLibrary(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shelves")
    public ResponseEntity<List<Shelf>> getAllShelves() {
        return ResponseEntity.ok(lookupService.getAllShelves());
    }

    @GetMapping("/shelves/library/{libraryId}")
    public ResponseEntity<List<Shelf>> getShelvesByLibrary(@PathVariable Long libraryId) {
        return ResponseEntity.ok(lookupService.getShelvesByLibrary(libraryId));
    }

    @PostMapping("/admin/shelves")
    public ResponseEntity<Shelf> saveShelf(@RequestBody Shelf shelf) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lookupService.saveShelf(shelf));
    }

    @PutMapping("/admin/shelves/{id}")
    public ResponseEntity<Shelf> updateShelf(
            @PathVariable Long id, @RequestBody Shelf shelf) {
        shelf.setId(id);
        return ResponseEntity.ok(lookupService.saveShelf(shelf));
    }

    @DeleteMapping("/admin/shelves/{id}")
    public ResponseEntity<Void> deleteShelf(@PathVariable Long id) {
        lookupService.deleteShelf(id);
        return ResponseEntity.noContent().build();
    }
}