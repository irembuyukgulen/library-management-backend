package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Sistem genelindeki yapılandırma ayarlarını tutar.
 */

@Data
@Entity
@Table(name = "system_settings")
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ayarın anahtarı — benzersiz olmalı.
     */
    @Column(nullable = false, unique = true)
    private String settingKey;

    /**
     * Ayarın değeri — her zaman String olarak saklanır.
     */
    @Column(nullable = false)
    private String settingValue;

    /** Ayarın ne işe yaradığını açıklayan metin */
    private String description;

    /**
     * Son güncellenme tarihi.
     */
    private LocalDateTime updatedAt;

    /**
     * Kayıt güncellendiğinde otomatik çalışır.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}