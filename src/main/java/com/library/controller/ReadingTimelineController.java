package com.library.controller;

import com.library.dto.ReadingTimelineResponse;
import com.library.service.ReadingTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Okuma zaman tüneli endpoint'lerini sunan Controller.
 * Endpoint'ler:
 * GET /api/users/{id}/reading-timeline       → Üye kendi zaman tüneli
 * GET /api/admin/users/{id}/reading-timeline → Admin herhangi kullanıcı
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReadingTimelineController {

    private final ReadingTimelineService readingTimelineService;

    /**
     * Kullanıcının okuma zaman tünelini getirir.
     * Üye kendi panelinde görür.
     */
    @GetMapping("/users/{id}/reading-timeline")
    public ResponseEntity<ReadingTimelineResponse> getTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(readingTimelineService.getTimeline(id));
    }

    /**
     * Admin herhangi bir kullanıcının zaman tünelini görür.
     */
    @GetMapping("/admin/users/{id}/reading-timeline")
    public ResponseEntity<ReadingTimelineResponse> getTimelineAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(readingTimelineService.getTimeline(id));
    }
}