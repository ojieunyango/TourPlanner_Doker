package com.example.tour_backend.domain.user;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);

    // 검색+정렬 지원
    List<User> findByNameContaining(String username, Sort sort);
    List<User> findByEmailContaining(String email, Sort sort);
}
