package com.example.tour_backend.dto.user;

import com.example.tour_backend.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Long userId;     // 사용자 ID 추가
    private String username; // 사용자 이름 추가
    private Role role;

}
