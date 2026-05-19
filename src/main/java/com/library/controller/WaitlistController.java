package com.library.controller;

import com.library.dto.WaitlistResponse;
import com.library.service.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;

    /** Bekleme listesine ekle */
    @PostMapping("/waitlist")
    public ResponseEntity<WaitlistResponse> addToWaitlist(
            @RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(
                waitlistService.addToWaitlist(body.get("bookId"), body.get("userId")));
    }

    /** Bekleme listesinden çıkar */
    @DeleteMapping("/waitlist")
    public ResponseEntity<Void> removeFromWaitlist(
            @RequestBody Map<String, Long> body) {
        waitlistService.removeFromWaitlist(body.get("bookId"), body.get("userId"));
        return ResponseEntity.noContent().build();
    }

    /** Kullanıcının bekleme listesi — bildirim sayfası için */
    @GetMapping("/waitlist/user/{userId}")
    public ResponseEntity<List<WaitlistResponse>> getUserWaitlist(
            @PathVariable Long userId) {
        return ResponseEntity.ok(waitlistService.getUserWaitlist(userId));
    }

    /** Kitap için bekleme listesinde mi? */
    @GetMapping("/waitlist/check")
    public ResponseEntity<Map<String, Boolean>> checkWaitlist(
            @RequestParam Long bookId,
            @RequestParam Long userId) {
        boolean inWaitlist = waitlistService.isUserInWaitlist(bookId, userId);
        return ResponseEntity.ok(Map.of("inWaitlist", inWaitlist));
    }
}