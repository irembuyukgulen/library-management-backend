package com.library.service;

import com.library.entity.AuditLog;
import com.library.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Audit log (denetim kaydı) yönetimini yapan servis.
 * Log formatı:
 * userEmail  → Kim yaptı?
 * action     → Ne yaptı? (BOOK_CREATED, USER_BANNED, RENTAL_RETURNED vb.)
 * entityType → Hangi tür varlık üzerinde? (Book, User, Rental vb.)
 * entityId   → Hangi kayıt üzerinde? (ID)
 * details    → Açıklama
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Yeni audit log kaydı oluşturur.
     */
    @Transactional
    public void log(String userEmail, String action,
                    String entityType, Long entityId, String details) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserEmail(userEmail);
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDetails(details);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Audit log kaydedilemedi: {} | {} | {} | {}",
                    userEmail, action, entityType, entityId, e);
        }
    }

    /**
     * Son 50 audit log kaydını getirir.
     */
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    /**
     * Belirli kullanıcının loglarını getirir.
     */
    public List<AuditLog> getLogsByUser(String email) {
        return auditLogRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    /**
     * Belirli entity tipinin loglarını getirir.
     */
    public List<AuditLog> getLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityTypeOrderByCreatedAtDesc(entityType);
    }

    /**
     * Belirli bir kaydın tüm loglarını getirir.
     */
    public List<AuditLog> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }
}