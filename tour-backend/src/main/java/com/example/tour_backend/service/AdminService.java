package com.example.tour_backend.service;

import com.example.tour_backend.domain.thread.Thread;
import com.example.tour_backend.domain.thread.ThreadRepository;
import com.example.tour_backend.domain.user.User;
import com.example.tour_backend.domain.user.UserRepository;
import com.example.tour_backend.domain.report.ReportRepository;
import com.example.tour_backend.domain.comment.Comment;
import com.example.tour_backend.domain.comment.CommentRepository;
import com.example.tour_backend.domain.thread.ThreadLikeRepository;
import com.example.tour_backend.domain.notification.NotificationRepository;
import com.example.tour_backend.dto.StatisticsResponseDto;
import com.example.tour_backend.dto.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final ThreadLikeRepository threadLikeRepository;
    private final NotificationRepository notificationRepository; // ✅ 추가

    /** 시스템 전체 통계 조회 */
    public StatisticsResponseDto getStatistics() {
        long userCount   = userRepository.count();
        long threadCount = threadRepository.count();
        long reportCount = reportRepository.count();
        return new StatisticsResponseDto(userCount, threadCount, reportCount);
    }

    /** 유저 리스트 조회 (검색/정렬 파라미터 포함) */
    public List<UserResponseDto> getUsers(String searchType, String keyword, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);

        List<com.example.tour_backend.domain.user.User> list;
        if (keyword != null && !keyword.isBlank()) {
            if ("email".equals(searchType)) {
                list = userRepository.findByEmailContaining(keyword, sort);
            } else {
                list = userRepository.findByNameContaining(keyword, sort);
            }
        } else {
            list = userRepository.findAll(sort);
        }

        return list.stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /** 유저 삭제 - 개선된 버전 (Notification 먼저 삭제) */
    @Transactional
    public void deleteUser(Long userId) {
        // 1. 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        System.out.println("✅ 사용자 삭제 시작: " + user.getUsername());

        try {
            // ✅ 2단계: 사용자가 작성한 댓글과 관련된 모든 알림 삭제
            List<Comment> userComments = commentRepository.findByUser_UserId(userId);
            for (Comment comment : userComments) {
                notificationRepository.deleteByComment_CommentId(comment.getCommentId());
            }
            System.out.println("✅ Comment 관련 Notification 삭제 완료");

            // ✅ 3단계: 사용자가 작성한 게시글과 관련된 모든 알림 삭제
            List<Thread> userThreads = threadRepository.findByUser_UserId(userId);
            for (Thread thread : userThreads) {
                notificationRepository.deleteByThread_ThreadId(thread.getThreadId());
            }
            System.out.println("✅ Thread 관련 Notification 삭제 완료");

            // ✅ 4단계: 사용자에게 온 모든 알림 삭제
            notificationRepository.deleteByUser_UserId(userId);
            System.out.println("✅ User 관련 Notification 삭제 완료");

            // 5단계: ThreadLike 삭제
            threadLikeRepository.deleteByUser_UserId(userId);
            System.out.println("✅ ThreadLike 삭제 완료");

            // 6단계: Comment 삭제 (이제 외래키 제약조건 없음)
            commentRepository.deleteByUser_UserId(userId);
            System.out.println("✅ Comment 삭제 완료");

            // 7단계: Thread 삭제
            threadRepository.deleteByUser_UserId(userId);
            System.out.println("✅ Thread 삭제 완료");

            // 8단계: User 삭제
            userRepository.deleteById(userId);
            System.out.println("✅ User 삭제 완료");

        } catch (Exception e) {
            System.err.println("❌ 사용자 삭제 실패: " + e.getMessage());
            throw new RuntimeException("사용자 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
