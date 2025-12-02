package com.reactpf.reactpf.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.reactpf.reactpf.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor                  // OncePerRequestFilter : 요청당 한 번만 실행되는 Spring Security 필터 클래스
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    
    private final JwtProvider jwtProvider; //jwt를 검증 하고 사용자명 추출하는 메소드가 잇는 클래스
    private final UserService userService; 


    //토큰을 담은 http요청의 헤더에서 jwt를 꺼내 검증하고
    //유효한 토큰이라면 SecurityContext에 인증정보를 세팅하는 메소드 
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //요청헤더에서 토큰을 추출한다
        String token = resolveToken(request);

        // 토큰이 존재하고, 유효한 경우에만 인증 처리를 수행한다
        if(token != null && jwtProvider.validateToken(token)) {
            //토큰에서 로그인된 사용자 id를 추출
            String userId = jwtProvider.getUsernameFromToken(token);
            //데이터베이스에 있는 사용자의 정보를 조회한다(userDto)
            UserDetails userDetails = userService.loadUserByUsername(userId);
            //spring security가 인식할 수 있는 인증 객체를 생성한다
            //     - principal : UserDetails
            //     - credentials : null (이미 인증되었기 때문)
            //     - authorities : 권한 목록
            UsernamePasswordAuthenticationToken authentication =    
                            new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }                                                                       // 서블린이란? 자바 웹 애플리케이션에서 HTTP 요청/응답을 처리하는 기본 단위
                                                                                // 그냥 spring 에서는 web.xml에 서블릿 하나에 컨트롤러 서비스등의 빈을 직접 등록해서 써야햇지만
                                                                                // srping boot는 자동으로 등록해주어서 훨씬 편하다
        //나머지 필터로 요청전달(다음 필터로 넘긴다) !!꼭 이걸 써야 다음 순서의 필터가 진행되고 서블릿까지 가서 url에 해당하는 컨트롤러가 실행된다
        filterChain.doFilter(request, response);
        
    }


    //요청헤더에서 토큰을 추출해주는 메소드
    private String resolveToken(HttpServletRequest request) {
        //요청헤더에서 보낸 Authorization의 값을 추출한다
        //보통  Authorization: "Bearer 토큰값"을 보낸다
    String bearerToken = request.getHeader("Authorization");
    //헤더가 존재하고, 값이 "Bearer "로 시작한다면 "Bearer " 뒤에 토큰만 잘라서 리턴
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7); 
    }
    return null;  // 토큰 없거나 잘못된 경우 null 반환한다
    }
}
