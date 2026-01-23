package com.example.toychat.user.jwt.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JwtToken {
    private String grantType; // Bearer
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn; // 엑세스 토큰 남은 시간
}
