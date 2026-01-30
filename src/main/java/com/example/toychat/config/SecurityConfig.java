package com.example.toychat.config;

import com.example.toychat.user.jwt.JwtAccessDeniedHandler;
import com.example.toychat.user.jwt.JwtAuthenticationEntryPoint;
import com.example.toychat.user.jwt.JwtAuthenticationFilter;
import com.example.toychat.user.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                // CORS 설정 연결
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))


                // JWT 방식이므로 CSRF 해제
                .csrf(AbstractHttpConfigurer::disable)

                // JSON으로 로그인 하므로 Form 로그인, HttpBasic 해제
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 사용 안 함
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 예외 처리 2개 등록
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // URL별 권한 설정 -> 로그인, 회원가입 관련 API는 누구나 접근 가능하게 허용 / 나머지 요청은 무조건 토큰 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/signup", "/api/users/login", "/api/users/reissue").permitAll()
                        .anyRequest().authenticated()
                )

                // 필터 등록
                // UsernamePasswordAuthenticationFilter 앞에 JwtAuthenticationFilter를 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /*
    Spring Security한테 주는 출입 명부의 역할
    */
    // CORS 설정 정의
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 프론트엔드 주소 허용(누가 들어올 수 있는가)
        config.setAllowedOrigins(List.of(("http://localhost:5500")));

        // GET, POST, PUT, DELETE 다 허용(어떠한 행동을 할 수 있는가)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        // 헤더 전부 허용
        config.setAllowedHeaders(List.of("*"));

        // 쿠키, 인증 정보 포함 허용
        config.setAllowCredentials(true);

        // 모든 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
