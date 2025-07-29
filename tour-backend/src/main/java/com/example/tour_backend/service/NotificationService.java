package com.example.tour_backend.service;

import com.example.tour_backend.domain.comment.Comment;
import com.example.tour_backend.domain.comment.CommentRepository;
import com.example.tour_backend.domain.notification.Notification;
import com.example.tour_backend.domain.notification.NotificationRepository;
import com.example.tour_backend.domain.thread.Thread;
import com.example.tour_backend.domain.thread.ThreadRepository;
import com.example.tour_backend.domain.user.User;
import com.example.tour_backend.domain.user.UserRepository;
import com.example.tour_backend.dto.notification.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public NotificationDto createNotification(Long userId, Long threadId, Long commentId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        Comment comment = null;
        if (commentId != null) {
            comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));
        }

        Notification notification = Notification.builder()
                .user(user)
                .thread(thread)
                .comment(comment)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        NotificationDto dto = new NotificationDto();
        dto.setNoticeId(notification.getNoticeId());
        dto.setUserId(user.getUserId());
        dto.setThreadId(thread.getThreadId());
        dto.setCommentId(commentId);
        dto.setMessage(message);
        dto.setRead(false);
        dto.setCreateDate(notification.getCreateDate());

        return dto;
    }

    public List<NotificationDto> getUserNotifications(Long userId) {
        List<Notification> notifications =
                notificationRepository.findByUserUserIdOrderByCreateDateDesc(userId);

        return notifications.stream()
                .map(n -> {
                    NotificationDto dto = new NotificationDto();
                    dto.setNoticeId(n.getNoticeId());
                    dto.setUserId(n.getUser().getUserId());
                    dto.setThreadId(n.getThread().getThreadId());
                    dto.setCommentId(n.getComment().getCommentId());
                    dto.setCommentId(n.getComment() != null ? n.getComment().getCommentId() : null); //7/3
                    dto.setMessage(n.getMessage());
                    dto.setRead(n.isRead());
                    dto.setCreateDate(n.getCreateDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    //알림 읽음 처리하는 메서드 7/7
    public void deleteNotificationsByIds(List<Long> ids) { //7/3
        notificationRepository.deleteAllById(ids);
    }

    @Transactional
    public void markAsRead(Long noticeId) {
        Notification notification = notificationRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("알림이 존재하지 않습니다."));

        if (!notification.isRead()) {
            notification.setRead(true); // 필드 직접 수정
        }
    }

}
