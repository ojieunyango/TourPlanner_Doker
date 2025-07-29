package com.example.tour_backend.domain.thread;

import com.example.tour_backend.domain.comment.Comment;
import com.example.tour_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "thread")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long threadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private String author;

    private int count = 0;
    private int heart = 0;


    private int commentCount = 0;
    private String area;

    @CreationTimestamp
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ThreadLike> likes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "thread_files", joinColumns = @JoinColumn(name = "thread_id"))
    @Column(name = "file_path")
    private List<String> filePaths = new ArrayList<>();// 파일 업로드

}
