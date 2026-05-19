package com.library.service;

import com.library.dto.WaitlistResponse;
import com.library.entity.*;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bekleme listesi iş mantığını yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WaitlistService {

    private final WaitlistNotificationRepository waitlistRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Kullanıcıyı bekleme listesine ekler.
     */
    @Transactional
    public WaitlistResponse addToWaitlist(Long bookId, Long userId) {
        Book book = bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        if (waitlistRepository.existsByBookIdAndUserIdAndNotifiedFalse(bookId, userId)) {
            throw new BusinessException("Bu kitap için zaten bildirim talebiniz var");
        }

        WaitlistNotification notification = new WaitlistNotification();
        notification.setBook(book);
        notification.setUser(user);
        notification.setNotified(false);

        return toResponse(waitlistRepository.save(notification));
    }

    /**
     * Kullanıcıyı bekleme listesinden çıkarır.
     */
    @Transactional
    public void removeFromWaitlist(Long bookId, Long userId) {
        WaitlistNotification notification = waitlistRepository
                .findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bekleme kaydı bulunamadı"));

        waitlistRepository.delete(notification);
    }

    /**
     * Kullanıcının bekleme listesini getirir.
     */
    public List<WaitlistResponse> getUserWaitlist(Long userId) {
        return waitlistRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kitap iade edildiğinde bekleyen tüm kullanıcıları bildirir.
     */
    @Transactional
    public void notifyWaitingUsers(Long bookId) {
        List<WaitlistNotification> waiting = waitlistRepository.findByBookIdAndNotifiedFalse(bookId);

        if (waiting.isEmpty()) return;

        waiting.forEach(w -> {
            w.setNotified(true);
            w.setNotifiedAt(LocalDateTime.now());
            waitlistRepository.save(w);
        });
    }

    /**
     * Kullanıcının bu kitap için aktif bekleme kaydı var mı kontrol eder.
     */
    public boolean isUserInWaitlist(Long bookId, Long userId) {
        return waitlistRepository.existsByBookIdAndUserIdAndNotifiedFalse(bookId, userId);
    }

    /**
     * WaitlistNotification entity'sini WaitlistResponse DTO'suna dönüştürür.
     */
    private WaitlistResponse toResponse(WaitlistNotification n) {
        WaitlistResponse response = new WaitlistResponse();
        response.setId(n.getId());
        response.setNotified(n.getNotified());
        response.setNotifiedAt(n.getNotifiedAt());
        response.setCreatedAt(n.getCreatedAt());

         try {
            if (n.getBook() != null) {
                response.setBookId(n.getBook().getId());
                response.setBookTitle(n.getBook().getTitle());
                response.setBookThumbnail(n.getBook().getThumbnail());
            }
        } catch (Exception e) {}

        return response;
    }
}