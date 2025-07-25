package com.example.tour_backend.service;

import com.example.tour_backend.domain.comment.Comment;
import com.example.tour_backend.domain.comment.CommentRepository;
import com.example.tour_backend.domain.thread.Thread;
import com.example.tour_backend.domain.thread.ThreadRepository;
import com.example.tour_backend.domain.user.User;
import com.example.tour_backend.domain.user.UserRepository;
import com.example.tour_backend.dto.comment.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository; // 댓글 데이터베이스 접근용 리포지토리
    private final ThreadRepository threadRepository; // 게시글 데이터베이스 접근용 리포지토리
    private final UserRepository userRepository; // ✅ 추가: 사용자 정보 조회용
    private final NotificationService notificationService; //7/3

    @Transactional
    public CommentDto addComment(CommentDto dto) {
        // 1. 댓글이 달릴 게시글 존재 여부 확인
        Thread thread = threadRepository.findById(dto.getThreadId())
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        // 2. 댓글 작성자 사용자 정보 조회 (userId 기반)
        if (dto.getUserId() == null) {
            throw new RuntimeException("userId가 없습니다.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        // ✅ 3. Comment 엔티티 생성 - user 필드 포함
        Comment comment = Comment.builder()
                .thread(thread)
                .comment(dto.getComment())
                .user(user) // ✅ user 필드 설정
                .build();

        // ✅ author 필드 명시적 설정 (빌더에서 자동 설정되지만 확실히 하기 위해)
        comment.setAuthor(user.getUsername());
        // 부모 댓글이 있을 경우 설정 (대댓글 등록 시) 7/2
        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글이 존재하지 않습니다."));
            comment.setParent(parent);
        }
        // 4. 댓글 저장
        commentRepository.save(comment);

        // ✅ 5. 알림 생성 - null 체크 추가
        if (thread.getUser() != null) {
            notificationService.createNotification(
                    thread.getUser().getUserId(),           // 게시글 작성자
                    thread.getThreadId(),                   // 게시글 ID
                    comment.getCommentId(),                 // 댓글 ID
                    comment.getAuthor() + "님이 댓글을 남겼습니다."
            );
        }

        // 6. 저장 후 DB에서 생성된 댓글ID, 생성일, 수정일을 DTO에 세팅해 반환
        dto.setCommentId(comment.getCommentId());
        dto.setCreateDate(comment.getCreateDate());
        dto.setModifiedDate(comment.getModifiedDate());
        dto.setAuthor(comment.getAuthor()); // ✅ user.username에서 가져온 값

        return dto;
    }

    @Transactional(readOnly = true) // 댓글 목록 조회 (대댓글 포함)7/2
    public List<CommentDto> getComments(Long threadId) {
        // 부모 댓글만 가져오고, 자식은 DTO 내부에서 트리 구조로 처리
        List<Comment> parents = commentRepository.findByThread_ThreadIdAndParentIsNull(threadId);
        return parents.stream().map(this::convertToDtoWithChildren).collect(Collectors.toList());
    }
    private CommentDto convertToDtoWithChildren(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setCommentId(comment.getCommentId());
        dto.setThreadId(comment.getThread().getThreadId());
        dto.setAuthor(comment.getAuthor()); // ✅ getAuthor() 메서드 사용
        dto.setComment(comment.getComment());
        dto.setCreateDate(comment.getCreateDate());
        dto.setModifiedDate(comment.getModifiedDate());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getCommentId() : null);
        dto.setUserId(comment.getUser() != null ? comment.getUser().getUserId() : null); // ✅ userId 설정

        // 대댓글 재귀적으로 처리
        List<CommentDto> children = comment.getChildren().stream()
                .map(this::convertToDtoWithChildren)
                .collect(Collectors.toList());

        dto.setComments(children);
        return dto;
    }

//        // 게시글 ID로 모든 댓글 조회 후 Comment -> CommentDto 변환
//        return commentRepository.findByThread_ThreadId(threadId)
//            .stream()
//                .map(comment -> {
//        CommentDto dto = new CommentDto();
//        dto.setCommentId(comment.getCommentId());
//        dto.setThreadId(comment.getThread().getThreadId());
//        dto.setAuthor(comment.getAuthor());
//        dto.setComment(comment.getComment());
//        dto.setCreateDate(comment.getCreateDate());
//        dto.setModifiedDate(comment.getModifiedDate());
//        return dto;
//    })
//            .collect(Collectors.toList());


    // ✅ 댓글 수정 - 권한 확인 로직 복원
    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto dto) {
        // 1. 댓글 존재 여부 확인 후 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        // ✅ 2. 작성자 본인인지 확인 (userId 기반)
        if (dto.getUserId() == null || !comment.getUser().getUserId().equals(dto.getUserId())) {
            throw new AccessDeniedException("본인만 수정할 수 있습니다.");
        }

        // 3. 댓글 내용 수정
        comment.setComment(dto.getComment());

        // 4. 저장 후 업데이트된 엔티티를 반환받아 DTO에 생성일, 수정일 세팅
        Comment updated = commentRepository.save(comment);

        dto.setCreateDate(updated.getCreateDate());
        dto.setModifiedDate(updated.getModifiedDate());
        dto.setAuthor(updated.getAuthor()); // ✅ user.username에서 가져온 값

        return dto;
    }
    // ✅ 댓글 삭제 - 권한 확인 로직 복원
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 1. 댓글 존재 여부 확인 후 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        // ✅ 2. 작성자 본인인지 확인 (userId 기반)
        if (userId == null || !comment.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("본인만 삭제할 수 있습니다.");
        }

        // 3. 댓글 삭제
        commentRepository.delete(comment);
    }


}
