package com.example.toychat.user.service;

import com.example.toychat.user.Entity.User;
import com.example.toychat.user.dto.LoginRequest;
import com.example.toychat.user.dto.SignupRequest;
import com.example.toychat.user.jwt.Entity.RefreshToken;
import com.example.toychat.user.jwt.dto.JwtToken;
import com.example.toychat.user.jwt.JwtTokenProvider;
import com.example.toychat.user.jwt.dto.TokenRequest;
import com.example.toychat.user.jwt.repository.RefreshTokenRepository;
import com.example.toychat.user.repository.UserRepository;
import com.example.toychat.user.security.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    // 회원가입 메서드
    public Long signup(SignupRequest request) {
        if(userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("이미 가입된 이메일입니다");
        }

        // 비밀번호 해시 처리
        String encodedPassword = passwordEncoder.encode(request.password());

        // Entity 저장
        User user = userRepository.save(request.toEntity(encodedPassword));

        // 생성이 완료되면 ID를 던져줌
        return user.getId();
    }

    // 로그인 메서드
    public JwtToken login(LoginRequest request) {
        // 사용자가 보낸 이메일과 비밀번호를 기반으로 AuthenticationToken 생성(인증 여부 = false)
        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthertication();

        /*
        authenticationManagerBuilder.getObject() -> 스프링 시큐리티의 인증을 총괗하는 AuthenticationManager를 호출
        .authenticate -> 실제 검증 로직이 실행되는 메서드로 id와 pw를 사용해서 검증한다
         */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(jwtToken.getRefreshToken())
                .email(authentication.getName())
                .build();

        refreshTokenRepository.save(refreshToken);

        return jwtToken;
    }

    public JwtToken reissue(TokenRequest tokenRequest) {
        // Refresh Token 검증
        if(!jwtTokenProvider.validateToken(tokenRequest.getRefreshToken())){
            throw new RuntimeException("유효하지 않은 Refresh Token입니다");
        }

        // Redis에서 토큰 조회
        RefreshToken refreshTokne = refreshTokenRepository.findById(tokenRequest.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다"));

        // 유저 정보 가져오기
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(refreshTokne.getEmail());

        UsernamePasswordAuthenticationToken authentication = new  UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 새로운 토큰 생성
        JwtToken newToken = jwtTokenProvider.generateToken(authentication);

        // Redis 업데이트
        // 1. 기존 토큰 삭제
        refreshTokenRepository.delete(refreshTokne);

        // 2. 새로운 토큰 저장
        RefreshToken newRefreshToken = RefreshToken.builder()
                .refreshToken(newToken.getRefreshToken())
                .email(authentication.getName())
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return newToken;
    }
}
