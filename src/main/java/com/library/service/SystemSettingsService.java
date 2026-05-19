package com.library.service;

import com.library.entity.SystemSettings;
import com.library.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Sistem ayarları yönetimini yapan servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository systemSettingsRepository;

    /**
     * Tüm sistem ayarlarını getirir.
     */
    public List<SystemSettings> getAllSettings() {
        return systemSettingsRepository.findAll();
    }

    /**
     * Tek bir ayarı günceller veya yoksa oluşturur.
     */
    @Transactional
    public SystemSettings saveSetting(String key, String value, String description) {
        SystemSettings setting = systemSettingsRepository
                .findBySettingKey(key)
                .orElse(new SystemSettings());

        setting.setSettingKey(key);
        setting.setSettingValue(value);

        if (description != null) {
            setting.setDescription(description);
        }

        SystemSettings saved = systemSettingsRepository.save(setting);

        log.info("Sistem ayarı güncellendi: {} = {}", key, value);

        return saved;
    }

    /**
     * Birden fazla ayarı aynı anda günceller.
     */
    @Transactional
    public void saveAllSettings(Map<String, String> settings) {
        settings.forEach((key, value) -> saveSetting(key, value, null));

        log.info("{} adet sistem ayarı güncellendi", settings.size());
    }
}