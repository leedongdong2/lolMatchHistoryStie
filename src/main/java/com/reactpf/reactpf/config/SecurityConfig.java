package com.reactpf.reactpf.config;


import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    private static final String[] AUTH_WHITLIST = {
        "/**","/user/**","/login", "/logout", "/"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable());
        http.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.authorizeHttpRequests(authorize -> authorize
                                    .requestMatchers(AUTH_WHITLIST).permitAll());

        //jwt발행과 spa방식에 맞춰서 직접 다이렉트 하지않게 커스텀 로그인을 구현하기위해 disable해줌
        http.formLogin(formLogin -> formLogin.disable());
        /* 
        http.formLogin(form->form
                       .loginPage("/login")
                       .loginProcessingUrl("/login_proc")
                       .successHandler(onAuthenticationSuccess)
                       .permitAll())
                       .logout(logout -> logout
                       .logoutUrl("/logout")
                       .logoutSuccessUrl("/")
                       .invalidateHttpSession(true)
                       .deleteCookies("JSESSIONID")
                       .permitAll());
        */
        http.oauth2Login(aouth2->aouth2.disable());
        //토큰을 필터하는부분
        http.addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);                          
        http.headers(header -> header
                     .frameOptions(frameOption -> frameOption
                                    .sameOrigin())
                     .crossOriginOpenerPolicy(coop -> coop.policy(CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.UNSAFE_NONE)) 
                     .crossOriginEmbedderPolicy(coep -> coep.policy(CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.UNSAFE_NONE)));
                
                    
                                                           
        
        return http.build();
    }
    //비밀번호 암호화
    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
    }

    //로그인을 할떄 로그인 정보가 맞으면 Authentication 객체를 반환해주는
    // 로그인 처리 객체
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
