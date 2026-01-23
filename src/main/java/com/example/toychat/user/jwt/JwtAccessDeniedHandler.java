package com.example.toychat.user.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /*
    * 왜 이렇게 따로 예외를 처리하냐
    * 이건 Filter 영역을 처리하기 위한 것임
    * 일반적은 예외 처리는 DispatcherServlet 영역에서 일을 처리 가능함
    * 그렇기에 Filter 영역에서 발생하는 예외는 따로 처리할 필요가 있음
    */

    // 일반 유저가 관리자 페이지에 접근하면 403 에러 투척
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
