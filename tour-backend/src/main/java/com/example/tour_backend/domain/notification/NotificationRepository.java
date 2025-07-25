package com.example.tour_backend.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUserIdOrderByCreateDateDesc(Long userId);
    // ✅ 수정: 올바른 필드명 사용
    void deleteByComment_CommentId(Long commentId);

    // ✅ 수정: 올바른 필드명 사용
    void deleteByThread_ThreadId(Long threadId);

    // ✅ 수정: 올바른 필드명 사용
    void deleteByUser_UserId(Long userId);
}
