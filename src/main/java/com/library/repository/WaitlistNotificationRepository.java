package com.library.repository;

import com.library.entity.WaitlistNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistNotificationRepository extends JpaRepository<WaitlistNotification, Long> {

    /** Kullanıcının belirli kitap için bekleme kaydı var mı? */
    Optional<WaitlistNotification> findByBookIdAndUserId(Long bookId, Long userId);

    /** Kitap için bildirim bekleyen kullanıcılar */
    List<WaitlistNotification> findByBookIdAndNotifiedFalse(Long bookId);

    /** Kullanıcının tüm bekleme kayıtları */
    List<WaitlistNotification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Kullanıcının belirli kitap için kaydı var mı kontrolü */
    boolean existsByBookIdAndUserIdAndNotifiedFalse(Long bookId, Long userId);

    void deleteByUserId(Long userId);
}