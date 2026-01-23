package com.example.toychat.user.jwt.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "refreshToken", timeToLive = 604800)
public class RefreshToken {
    @Id
    private String refreshToken; // 토큰 값 자체를 ID로 사용

    private String email; // 어느 유저의 것인지 알기 위해서 이메일을 저장
}
