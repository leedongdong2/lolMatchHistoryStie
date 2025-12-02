package com.reactpf.reactpf.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtProvider {

    private final SecretKey secretKey; 
                        //jwt 서명을 위한 secret객체이다 나은 보안을 위해 yml이나  properties에 키를 정의해서 가져온다
    public JwtProvider(@Value("${jwt.secret}") String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(Authentication authentication) {
        //로그인된 사용자 정보를 가져온다
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        //jwt토큰을 발행한다
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())//토큰을 발행할 주체(사용자id)
                .setIssuedAt(new Date())//토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))//토큰 만료기간 1일
                .signWith(secretKey,SignatureAlgorithm.HS512)//서명 알고리즘 및 키 설정
                .compact();//jwt문자열로 압축한다
    }

        //이건 구글로 로그인을 하는경우
        public String googleGenerateToken(Authentication authentication) {
        //구글은 구글 이메일이 id라 이메일을 넣어지게됨 그래서 string으로 받아줌
        String userPrincipal = (String) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))//1일
                .signWith(secretKey,SignatureAlgorithm.HS512)
                .compact();
    }

    //토큰을 파싱해서 setSubject(userPrincipal) 를 꺼낼수 잇다 (내가 넣은 userId);
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
               .setSigningKey(secretKey)
               .build()
               .parseClaimsJws(token)
               .getBody()
               .getSubject();    
        }

    //토큰의 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true; // 파싱이 문제없으면 유효 토큰
        } catch (Exception e) {
            return false; // 만료나 서명 불일치 시 오류 발생(토큰이 유효하지않다)
        }
    }

    
}
