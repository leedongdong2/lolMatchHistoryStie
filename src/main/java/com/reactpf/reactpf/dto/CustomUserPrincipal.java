package com.reactpf.reactpf.dto;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;




public class CustomUserPrincipal implements OAuth2User, UserDetails {

    
    private UserDto user; // 우리가 사용하는 사용자 정보 DTO
    private Map<String, Object> attributes; // OAuth2 정보

    public CustomUserPrincipal(UserDto user) {
        this.user = user;
    }

    public CustomUserPrincipal(UserDto user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public UserDto getUser() {
        return user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return user.getAuthorities();
    }

    @Override
    public String getName() {
       return user.getUsername(); // email이나 id
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // email이나 id
    }
    

    public String getNickname() {
        return user.getNickname();
    }
}
