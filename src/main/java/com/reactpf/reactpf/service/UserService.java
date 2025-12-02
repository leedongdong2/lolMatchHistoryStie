package com.reactpf.reactpf.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.reactpf.reactpf.dto.CustomUserPrincipal;
import com.reactpf.reactpf.dto.UserDto;
import com.reactpf.reactpf.mapper.UserMapper;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    UserMapper userMapper;


    //아이디 중복체크
    public String idCheckAvailability(String tnName) {
        String result = userMapper.idCheckAvailability(tnName);
        return result;
    }

    public String nickNameCheckAvailability(String nickname) {
        String result = userMapper.nicknameCheckAvailability(nickname);
        return result;
    }

    public int getGoogleUser(String email) {
        int result = userMapper.getGoogleUser(email);
        return result;
    }

    //회원가입
    public String saveUser(HashMap<String, Object> paramMap) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            paramMap.put("tnPassword", bCryptPasswordEncoder.encode((String)paramMap.get("tnPassword")));
            int result = userMapper.saveUser(paramMap);
            if(result == 1){
                return "가입되었습니다";
            } else {
                return "오류";
            }
    }
    
    //유저 정보를 반환
    public UserDto getUserInfo(String userId) {
        return userMapper.getUserInfo(userId);
    }

    //로그인 할때 db에서 유저 정보를 꺼내 Spring Security가 사용할 수 있는 형태(UserDetails)로 변환
    @Override
    public UserDetails loadUserByUsername(String tnName) throws UsernameNotFoundException {
        UserDto userDto = userMapper.loginInfo(tnName);
        if(userDto == null) {
			throw new UsernameNotFoundException("User not authorized.");
		}
        return new CustomUserPrincipal(userDto);
        /**
        return User.builder()
                    .username(userDto.getTnName())
                    .password(userDto.getPassword())
                    .build();
                     */
    }   



}
