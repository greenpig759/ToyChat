package com.example.toychat.user.repository;

import com.example.toychat.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 유무 확인
    boolean existsByEmail(String email);

    // 유저 정보 가져오기
    Optional<User> findByEmail(String email);
}
