package com.example.tour_backend.dto.user;

import com.example.tour_backend.domain.Role;
import com.example.tour_backend.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String username;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String nickname;
    private Role role;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    /**
     * 엔티티 → DTO 변환용 생성자
     * (필드가 늘어나거나 바뀌면 이쪽만 고쳐 주시면 됩니다)
     */
    public UserResponseDto(User u) {
        this.userId       = u.getUserId();
        this.username     = u.getUsername();
        this.name         = u.getName();
        this.email        = u.getEmail();
        this.password     = u.getPassword();
        this.phone        = u.getPhone();
        this.nickname     = u.getNickname();
        this.role         = u.getRole();
        this.createDate   = u.getCreateDate();
        this.modifiedDate = u.getModifiedDate();
    }

    /**
     * Stream 매핑용 static 팩토리 메서드
     */
    public static UserResponseDto fromEntity(User u) {
        return new UserResponseDto(
                u.getUserId(),
                u.getUsername(),
                u.getName(),
                u.getEmail(),
                u.getPassword(),
                u.getPhone(),
                u.getNickname(),
                u.getRole(),
                u.getCreateDate(),
                u.getModifiedDate()
        );
    }
}
