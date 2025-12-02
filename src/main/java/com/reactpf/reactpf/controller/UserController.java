package com.reactpf.reactpf.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reactpf.reactpf.config.GoogleTokenVerifier;
import com.reactpf.reactpf.config.JwtProvider;
import com.reactpf.reactpf.dto.RiotUserDto;
import com.reactpf.reactpf.dto.UserDto;
import com.reactpf.reactpf.service.RiotWebApiService;
import com.reactpf.reactpf.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor//생성자를 자동으로 만들어줌(롬복)
public class UserController {

    private final UserService userService;
    private final RiotWebApiService riotWebApiService;
    private final AuthenticationManager authenticationManager;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final JwtProvider jwtProvider;



    //로그인 처리 기능
    @PostMapping("/api/login")
    public ResponseEntity<Map<String,String>> loginApi(@RequestBody Map<String,String> userData) {
        
        //받아온 사용자 아이디와 비밀번호를 authenticationManager 전달하여 db를 이용해 사용자를 조회하고 비밀번호 체크
        //성공하면 authentication 를 반환하여 준다
        //UsernamePasswordAuthenticationToken(스프링 시큐리티가 이해할 수 있는 형태로 포장하는 객체)
        //authenticationManager에 넘겨서 인증을 시도하게한다 
        //쭉쭊쭉 해서 유저 서비스에 잇는 loadUserByUsername를 실행함
        //원래 시큐리티 로그인을 자동으로 되게하면 해주는 거지만 토큰을 발행해야하기도 하고 
        //react 에서는 view를 백서버에서 옮기는거보다 react자체에서 view를 조정해야하니 
        //값 자체를 넘기기 위해 커스터마이징을 하기위해 수동으로 직접 써준다
        //그로인해 successhandler 이런거도 필요없다
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userData.get("username"), userData.get("password"))
        );
        //SecurityContext에 인증 정보 저장을 하여준다
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //authentication의 userId를 이용하여 토큰을 생성해준다
        String jwt = jwtProvider.generateToken(authentication);
        
        //토큰을 담아 넘겨준다
        Map<String,String> data = new HashMap<>();
        data.put("token", jwt);
        
        return ResponseEntity.ok(data);
    }
    
    @PostMapping("/api/google")
    public ResponseEntity<Map<String,String>> googleLogin(@RequestBody Map<String, String> body) {
         Map<String, String> data = new HashMap<>();
        //구글 id 토큰을 받아옴
        String googleToken = body.get("token");
        
        //구글 유저 검증
        String googleUser = googleTokenVerifier.verify(googleToken);
        if (googleUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int user = userService.getGoogleUser(googleUser);
        user = 0;

        if(user == 0) {
            return ResponseEntity.ok(Map.ofEntries(
                                                    Map.entry("status", "notUser"),
                                                    Map.entry("googleId", googleUser)
                                                    ));
        }

        System.out.println("이 로그가 찍히면 return 이후에도 실행된 것임!");

        // 2. 사용자 정보로 Authentication 생성 (또는 회원가입 로직 수행)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                googleUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        
        
        //아이디를 이용하여 토큰생성
        String jwt = jwtProvider.googleGenerateToken(authentication);

        //토큰을 담아 넘겨줌
        data.put("token", jwt);

        return ResponseEntity.ok(data);
    }

        //아이디 중복체크
    @GetMapping("/signUp/idCheck")
    public ResponseEntity<?> idCheckAvailability(@RequestParam("tnName") String tnName) {
        String result = userService.idCheckAvailability(tnName); 
        return ResponseEntity.ok(result);
    }
    

        //닉네임 중복체크
    @GetMapping("/signUp/nicknameCheck")
    public ResponseEntity<?> getMethodName(@RequestParam("nickname") String nickname) {
        String result = userService.nickNameCheckAvailability(nickname);
        return ResponseEntity.ok(result);
    }
    

    @GetMapping("/signUp/lolNameCheck")
    public ResponseEntity<?> lolNameCheckAvailability(UserDto userDto) {
        String lolNameTag = userDto.getLolNametag().substring(1);
        String region = userDto.getRegion().split("/")[0];
        RiotUserDto riotUserDto = riotWebApiService.getPuuId(userDto.getLolName(), lolNameTag, region);
        if(riotUserDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("없는 아이디입니다");
        }
         System.out.println(riotUserDto.getGameName());
        return ResponseEntity.ok("확인되었습니다");
    }

        //회원가입을 처리한다
    @PostMapping("/signUp")
    public String insertUser(@RequestBody HashMap<String,Object> hashMap) {
        String result = userService.saveUser(hashMap);
        return result;
    }


}
