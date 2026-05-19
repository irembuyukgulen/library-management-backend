package com.library.repository;

import com.library.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Sistem ayarları veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {

    /**
     * Anahtar (key) değerine göre sistem ayarını bulur.
     */
    Optional<SystemSettings> findBySettingKey(String settingKey);
}