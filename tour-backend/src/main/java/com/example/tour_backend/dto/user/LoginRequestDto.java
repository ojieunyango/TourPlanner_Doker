package com.example.tour_backend.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestDto {
    private String username;  // 보통 아이디나 이메일
    private String password;

}