package com.reactpf.reactpf.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto implements UserDetails{
    
    private String tnName;
    private String tnPassword;
    private String region;
    private String lolName;
    private String lolNametag;
    private String role;
    private String nickname;

    //사용자 권한 정보  반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(role));
    }
    // 비밀번호 반환(인증 이나 로그인시 비밀번호 확인용)
    @Override
    public String getPassword() {
        return this.tnPassword;
    }
    //아이디 반환(인증 이나 로그인시 아이디를 필요시 사용)
    @Override
    public String getUsername() {
        return this.tnName;
    }

}
