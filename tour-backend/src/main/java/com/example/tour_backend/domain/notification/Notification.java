package com.example.tour_backend.domain.notification;

import com.example.tour_backend.domain.comment.Comment;
import com.example.tour_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.example.tour_backend.domain.thread.Thread;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter @Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    // 수신 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user; // 수신자

    // 관련 게시물
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "threadId", nullable = false)
    private Thread thread;

    // 관련 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId", nullable = false)
    private Comment comment;

    @Column(nullable = false, length = 225)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createDate;

    @Builder
    public Notification(User user, Thread thread, Comment comment,
                        String message, boolean isRead, LocalDateTime createDate) {
        this.user = user;
        this.thread = thread;
        this.comment = comment;
        this.message = message;
        this.isRead = isRead;
        this.createDate = createDate;
    }

}