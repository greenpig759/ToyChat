package com.example.toychat.user.dto;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public record LoginRequest(
        String email,
        String password
) {
    // 이메일과 비밀번호를 기반으로 인증 토큰 객체 생성
    public UsernamePasswordAuthenticationToken toAuthertication(){
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
