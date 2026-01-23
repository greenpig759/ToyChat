package com.example.toychat.user.controller;

import com.example.toychat.user.dto.LoginRequest;
import com.example.toychat.user.dto.SignupRequest;
import com.example.toychat.user.jwt.dto.JwtToken;
import com.example.toychat.user.jwt.dto.TokenRequest;
import com.example.toychat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원가입 요청
    @PostMapping("/signup")
    public ResponseEntity<Long> singup(@RequestBody SignupRequest request){
        // 서비스 로직 호출
        Long id = userService.signup(request);

        // 201(새로운 데이터 생성) 상태 코드와 함께 id 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // 로그인 요청
    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(userService.login(request));
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<JwtToken> reissue(@RequestBody TokenRequest request){
        return ResponseEntity.ok(userService.reissue(request));
    }
}
