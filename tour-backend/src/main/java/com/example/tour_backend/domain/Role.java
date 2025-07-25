package com.example.tour_backend.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "손님"), //비회원
    USER("ROLE_USER", "일반사용자"),//회원
    ADMIN("ROLE_MANAGER", "관리자");//관리자


    private final String key;
    private final String title;

}