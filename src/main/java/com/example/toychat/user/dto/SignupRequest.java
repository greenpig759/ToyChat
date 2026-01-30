package com.example.toychat.user.dto;

import com.example.toychat.user.entity.Role;
import com.example.toychat.user.entity.User;


public record SignupRequest(
        String email,
        String password,
        String nickname
) {
    // DTO -> Entity 변환 메서드
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .role(Role.ROLE_USER)
                .build();
    }
}
