package com.example.toychat.user.jwt;

import com.example.toychat.user.jwt.dto.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// 토큰 생성, 파싱, 유효성 검사 담당
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String ROLE_KEY = "role";
    private static final String BEARER_KEY = "Bearer";
    private final long ACCESS_TOKEN_EXPIRE_TIME;
    private final long REFRESH_TOKEN_EXPIRE_TIME;
    private final Key key;

    /*
    * 암호화 할 때 2진수를 사용, 근데 설정 파일에는 이진수 데이터를 직접 적을 수가 없다 그러므로 Base64라는 방식으로 인코딩한 문자열이 즉 jwt.secret
    * 그래서 KeyBytes에 디코딩을 하는 이유가 이것이다
    * key에서는 KeyBytes(이건 그냥 바이트 배열 데이터 쪼가리)를 자바 보안 시스템이 사용 가능한 SecretKey 객체로 변환(이때 자동으로 길이 검사도 수행)
    */
    // 생성자: Secret Key를 세팅
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey
    ,@Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenTime
    ,@Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTokenTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTokenTime;
    }

    // 토큰 생성: 로그인 성공 시 호출됨
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        /*
        * authentication이란 현재 접속한 사용자 그 자체를 나타내는 가장 중요한 인터페이스
        * principal, credintials, Authorities, Authenticated 총 4가지 정보가 들어있음
        * authentication.getAuthorities가 주는 것은 Collection<? extends GrantedAuthority> 타입의 객체 리스트
        * 지금 토큰에 넣으려는 것은 그냥 ROLE_뭐시기 형태의 간단한 문자열이므로 타입을 맞게 해주기 위해 저 코드가 필요
        * .stream -> 스트림 시작
        * .map -> authority만 받기 위해 사용
        * .collection -
        * */

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // 사용자 ID
                .claim(ROLE_KEY, authorities) // 권한(ROLE)
                .setExpiration(accessTokenExpiresIn) // 만료 시간(유효기간 설정)
                .signWith(key, SignatureAlgorithm.HS512) // 암호화 종류
                .compact(); // 문자열로 변환

        // Refresh Token 생성(최소한의 정보로 유효기간만 길게 잡기)
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return JwtToken.builder()
                .grantType(BEARER_KEY)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    // 인증 정보 조회(필터에서 토큰을 까서 사용자가 누군지 알아낼 때 사용)
    public Authentication getAuthentication(String accessToken){
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if(claims.get(ROLE_KEY) == null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다");
        }

        // 클레임에서 권한 정보 가져오기(String -> Object)
        // 스프링 시큐리티는 GrantedAuthority 인터페이스를 구현한 객체만 권한으로 취급
        // 배열에서 값을 가져와서 콤마를 기준으로 나누고 하나씩 처리
        Collection<? extends  GrantedAuthority> authorities =
        Arrays.stream(claims.get(ROLE_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new) // 들어오는 글자마다 생성자를 실행(람다식 형태)
                .collect(Collectors.toList()); // 변환한 객체들을 모아서 리스트로 만든다

        // UserDetails 객체를 만들어서 Authentication 리턴
        // 이때의 User는 Spring Security가 제공하는 User임(아이디, 비밀번호, 권한목록)
        // 이미 토큰 검증 단계(validateToken)에서 서명 확인이 끝났기에 비밀번호는 없어도 ㄱㅊ
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // 최종 출입증 발급(Principal, Credentials, Authorities로 구성)
        // 이 Authentication 객체는 SecurityContextHolder라는 전역 저장소에 저장
        return new UsernamePasswordAuthenticationToken(principal,"", authorities);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("잘못된 JWT 서명입니다");
        }catch(ExpiredJwtException e){
            log.info("만료된 JWT 토큰입니다");
        }catch(UnsupportedJwtException e){
            log.info("지원되지 않는 JWT 토큰입니다");
        }catch(IllegalArgumentException e){
            log.info("JWT 토큰이 잘못되었습니다");
        }
        return false;
    }

    // 토큰 파싱 메서드
    // 암호화 된 문자열을 풀어서 자바가 이해 가능한 데이터 덩어리(Claims)로 바꿔준다
    // Claims는 jjwt 라이브러리가 제공하는 인터페이스
    // 사실상 Map<String, Object>와 동일
    private Claims parseClaims(String accessToken){
        try{
            // 열쇠 준비 -> 유효성 검사 -> 내용물 꺼내기 단계
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e){
            // 만료되긴 했지만 들어있던 claims를 주어 재발급 등의 일이 가능하게 처리
            // 유효기간이 지난 예외이지만 객체 e 안에는 만료되기 전의 정보가 들어있다
            return e.getClaims();
        }
    }
}
