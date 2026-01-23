package com.example.toychat.user.security;

import com.example.toychat.user.Entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getUsername(){
        return user.getEmail();
    }

    @Override
    public String getPassword(){
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {return true;} // 계정 만료 여부

    @Override
    public boolean isAccountNonLocked() {return true;} // 계정 잠김 여부

    @Override
    public boolean isCredentialsNonExpired() {return true;} // 비밀번호 만료 여부

    @Override
    public boolean isEnabled() {return true;} // 계정 활성화 여부
}
