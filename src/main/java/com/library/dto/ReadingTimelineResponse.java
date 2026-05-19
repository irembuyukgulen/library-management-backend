package com.library.dto;

import lombok.Data;
import java.util.Map;

/**
 * Kullanıcının okuma zaman tüneli için DTO.
 * aylık sayfa ve kitap sayıları döndürülür.
 * Hesaplama: Sadece RETURNED durumundaki kiralamalar sayılır.
 * Aktif kiralama henüz bitmediği için dahil edilmez.
 */
@Data
public class ReadingTimelineResponse {

    /** Bu yıl okunan kitap sayısı */
    private int currentYearBooks;

    /** Geçen yıl okunan kitap sayısı — karşılaştırma için */
    private int lastYearBooks;

    /** Bu yıl okunan toplam sayfa sayısı */
    private int currentYearPages;

    /** Geçen yıl okunan toplam sayfa sayısı */
    private int lastYearPages;

    /**
     * Geçen yıla göre gelişim yüzdesi.
     * Pozitif → daha çok okudu
     * Negatif → daha az okudu
     */
    private double improvementPercent;

    /**
     * Kullanıcıya gösterilecek motivasyon mesajı.
     */
    private String motivationMessage;
    /**
     * Aylık sayfa sayısı haritası.
     */
    private Map<String, Integer> monthlyPageMap;

    /**
     * Aylık kitap sayısı haritası.
     */
    private Map<String, Integer> monthlyBookMap;

    /** En çok okunan kitap kategorisi */
    private String favoriteCategory;

    /** Toplam okunan kitap sayısı (tüm zamanlar) */
    private int totalBooksRead;

    /** Toplam okunan sayfa sayısı (tüm zamanlar) */
    private int totalPagesRead;

    /** En uzun okunan kitabın başlığı */
    private String longestBookTitle;

    /** En uzun okunan kitabın sayfa sayısı */
    private Integer longestBookPages;
}