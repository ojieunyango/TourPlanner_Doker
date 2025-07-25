package com.example.tour_backend.domain.comment;

import com.example.tour_backend.domain.notification.Notification;
import com.example.tour_backend.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.tour_backend.domain.thread.Thread;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "threadId", nullable = false)
    @JsonIgnore // 무한참조 방지 7/2
    private Thread thread;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Getter
    @Column(nullable = false)
    private String author;

    @CreationTimestamp
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    // 7/2 대댓글 기능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")  // 부모 댓글 참조
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    // 📝 댓글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // === 여기에 Notification과의 양방향 관계 추가 ===
    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();
    // cascade = REMOVE: 댓글 삭제 시 관련 알림도 자동 삭제
    // orphanRemoval = true: 알림 엔티티 관리 편리하게 하기 위함

    @Builder
    public Comment(Thread thread, String comment, String author,User user,
                   LocalDateTime createDate, LocalDateTime modifiedDate) {
        this.thread = thread;
        this.comment = comment;
        this.author = author;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
        this.user = user;
    }
    public String getAuthor() {
        return author;
    }
}



