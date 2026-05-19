package com.library.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.BookRequest;
import com.library.dto.GoogleBookResponse;
import com.library.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Google Books API entegrasyonunu yöneten servis.
 */
@Slf4j
@Service
public class GoogleBooksService {

    @Value("${google.books.api.key}")
    private String apiKey;

    @Value("${google.books.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ISBN numarasıyla Google Books API'den kitap bilgisi çeker.
     */
    public GoogleBookResponse getBookByIsbn(String isbn) {
        try {
            String url = apiUrl + "?q=isbn:" + isbn + "&key=" + apiKey;
            log.debug("Google Books API isteği: {}", url);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            int totalItems = root.path("totalItems").asInt();
            if (totalItems == 0) {
                throw new ResourceNotFoundException(
                        "Bu ISBN için kitap bulunamadı: " + isbn);
            }

            JsonNode volumeInfo = root.path("items").get(0).path("volumeInfo");
            GoogleBookResponse book = new GoogleBookResponse();

            book.setTitle(volumeInfo.path("title").asText(""));

            JsonNode authorsNode = volumeInfo.path("authors");
            if (authorsNode.isArray() && authorsNode.size() > 0) {
                StringBuilder authors = new StringBuilder();
                for (int i = 0; i < authorsNode.size(); i++) {
                    if (i > 0) authors.append(", ");
                    authors.append(authorsNode.get(i).asText());
                }
                book.setAuthors(authors.toString());
            }

            book.setPublisher(volumeInfo.path("publisher").asText(""));

            book.setPublishedDate(volumeInfo.path("publishedDate").asText(""));

            book.setDescription(volumeInfo.path("description").asText(""));

            book.setPageCount(volumeInfo.path("pageCount").asInt(0));

            book.setLanguage(volumeInfo.path("language").asText(""));

            JsonNode categoriesNode = volumeInfo.path("categories");
            if (categoriesNode.isArray() && categoriesNode.size() > 0) {
                book.setCategories(categoriesNode.get(0).asText());
            }

            JsonNode imageLinks = volumeInfo.path("imageLinks");
            if (!imageLinks.isMissingNode()) {
                book.setThumbnail(selectBestImage(imageLinks));
            }

            book.setIsbn(isbn);

            log.info("Google Books'tan kitap çekildi: {} (ISBN: {})", book.getTitle(), isbn);
            return book;

        } catch (ResourceNotFoundException e) {
            throw e; // ResourceNotFoundException'ı yeniden fırlat
        } catch (Exception e) {
            log.error("Google Books API hatası: {}", e.getMessage());
            throw new RuntimeException("Google Books API'den veri alınamadı: " + e.getMessage());
        }
    }

    /**
     * ISBN ile Google Books'tan kitap bilgisi çekip BookRequest'e dönüştürür.
     */
    public BookRequest getBookRequestByIsbn(String isbn) {
        GoogleBookResponse googleBook = getBookByIsbn(isbn);

        BookRequest request = new BookRequest();
        request.setTitle(googleBook.getTitle());
        request.setIsbn(googleBook.getIsbn());
        request.setAuthorName(googleBook.getAuthors());
        request.setPublisherName(googleBook.getPublisher());
        request.setCategoryName(googleBook.getCategories());
        request.setDescription(googleBook.getDescription());
        request.setPageCount(googleBook.getPageCount());
        request.setThumbnail(googleBook.getThumbnail());

        return request;
    }

    /**
     * Google Books'tan gelen imageLinks'ten en kaliteli resmi seçer.
     */
    private String selectBestImage(JsonNode imageLinks) {
        String[] priorities = {"extraLarge", "large", "medium", "thumbnail"};

        for (String size : priorities) {
            String url = imageLinks.path(size).asText("");
            if (!url.isEmpty()) {
                return url.replace("http://", "https://");
            }
        }
        return "";
    }
}