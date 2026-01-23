package com.example.toychat.user.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 로그인을 안 한 사람, 토큰이 만료된 사람, 토큰이 위조된 사람을 막을때 사용, 401 에러 투척
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 유효한 자격증명을 제공하지 않고 접근하려 할 때 401 Unauthorized 에러를 리턴
    }

}
