package com.example.tour_backend.dto.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {
    private String username;
    private String password;
    private String email;
    private String name;
    private String phone;
    private String nickname;

}

