package com.example.tour_backend.domain.report;


import com.example.tour_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports") // 테이블 이름은 reports
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId; // 신고 ID (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // 신고자 (User FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private User target; // 신고당한 사용자 (User FK)

    @Column(nullable = false, length = 255)
    private String reason; // 신고 사유

    @CreationTimestamp
    private LocalDateTime createdAt; // 신고 생성일시
}

