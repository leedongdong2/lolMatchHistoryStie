package com.reactpf.reactpf.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import com.reactpf.reactpf.dto.UserDto;

@Mapper
public interface UserMapper {
    public int saveUser(HashMap<String,Object> userData);
    public UserDto loginInfo(String userId);
    public UserDto getUserInfo(String userId);
    public String idCheckAvailability(String tnName);
    public String nicknameCheckAvailability(String nickname);
    public UserDto loginEmailCheck(String email);
    public int getGoogleUser(String email);
} 


