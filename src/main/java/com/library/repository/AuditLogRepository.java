package com.library.repository;

import com.library.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Audit log veritabanı işlemlerini yöneten Repository.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Belirli kullanıcının loglarını getirir — en yeniden eskiye.
     */
    List<AuditLog> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    /**
     * Belirli entity tipinin loglarını getirir.
     */
    List<AuditLog> findByEntityTypeOrderByCreatedAtDesc(String entityType);

    /**
     * Belirli bir kaydın tüm loglarını getirir.
     */
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);

    /**
     * Son 50 log kaydını getirir — admin dashboard için.
     */
    List<AuditLog> findTop50ByOrderByCreatedAtDesc();

    void deleteByUserEmail(String email);
}