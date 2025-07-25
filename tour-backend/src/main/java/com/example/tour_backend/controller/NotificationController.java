package com.example.tour_backend.controller;

import com.example.tour_backend.dto.notification.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tour_backend.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
        List<NotificationDto> list = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(list);
    }
    @DeleteMapping // 7/3
    public ResponseEntity<Void> deleteNotifications(@RequestBody List<Long> ids) {
        notificationService.deleteNotificationsByIds(ids);
        return ResponseEntity.noContent().build();
    }
    // 알림 읽음 처리하는 메서드 7/7
    @PatchMapping("/{noticeId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long noticeId) {
        notificationService.markAsRead(noticeId);
        return ResponseEntity.noContent().build();
    }
}