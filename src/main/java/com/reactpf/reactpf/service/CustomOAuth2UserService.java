package com.reactpf.reactpf.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

import com.reactpf.reactpf.dto.CustomUserPrincipal;
import com.reactpf.reactpf.dto.UserDto;
import com.reactpf.reactpf.mapper.UserMapper;
import org.springframework.security.oauth2.core.user.OAuth2User;

import jakarta.servlet.http.HttpSession;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService  {
    
    private final UserMapper userMapper;

    public CustomOAuth2UserService(UserMapper userMapper, HttpSession httpSession) {
        this.userMapper = userMapper;
    }
    

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");

        UserDto user = userMapper.loginEmailCheck(email);

        


        return new CustomUserPrincipal(user, oAuth2User.getAttributes());
    }


}