package com.example.tour_backend.domain.thread;

import com.example.tour_backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ThreadLikeRepository extends JpaRepository<ThreadLike, Long> {
    Optional<ThreadLike> findByUserAndThread(User user, Thread thread);
    boolean existsByUserAndThread(User user, Thread thread);

    void deleteByUserAndThread(User user, Thread thread);
    // ✅ 추가: 사용자 ID로 좋아요 삭제
    void deleteByUser_UserId(Long userId); //??

    // 좋아요 누른 게시글만 바로 조회 (Thread 엔티티로 반환)
    List<ThreadLike> findByUser_UserId(Long userId);

    // 만약 Thread만 반환하고 싶다면 (JPQL)
    @Query("SELECT tl.thread FROM ThreadLike tl WHERE tl.user.userId = :userId")
    List<Thread> findThreadsByUserId(@Param("userId") Long userId);
}

//좋아요 여부 체크 및 취소할 수 있도록 쿼리 메서드 제공