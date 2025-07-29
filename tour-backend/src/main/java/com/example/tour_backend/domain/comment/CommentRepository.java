package com.example.tour_backend.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByThread_ThreadId(Long threadId); //6/30
    List<Comment> findByParent_CommentId(Long parentId); //특정 부모 댓글(parentId)을 가진 자식 댓글들(대댓글) 을 조회 7/2
    List<Comment> findByThread_ThreadIdAndParentIsNull(Long threadId);
    //특정 게시글(threadId)에 속하면서, 부모 댓글이 없는 최상위 댓글(대댓글이 아닌 일반 댓글) 만 조회 7/2
    // ✅ 추가: 사용자 ID로 댓글 삭제
    void deleteByUser_UserId(Long userId);

    // ✅ 추가: 사용자 ID로 댓글 조회 (알림 삭제를 위해)
    List<Comment> findByUser_UserId(Long userId);
}