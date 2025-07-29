package com.example.tour_backend.domain.thread;

import com.example.tour_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;
import jakarta.persistence.Id;
//어떤 유저가 어떤 게시글에 좋아요를 눌렀는지 기록
@Entity
@Table(name = "thread_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "thread_id"}) //한 유저가 한 글에 여러 번 좋아요 못 누르도록 제한
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreadLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private Thread thread;


    // equals/hashCode (중복 방지) user + thread 조합이 유일하도록
    //JPA와 Set, Map에서 중복 없이 비교 가능하도록 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThreadLike)) return false;
        ThreadLike that = (ThreadLike) o;
        return Objects.equals(user.getUserId(), that.user.getUserId()) &&
                Objects.equals(thread.getThreadId(), that.thread.getThreadId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getUserId(), thread.getThreadId());
    }
}
