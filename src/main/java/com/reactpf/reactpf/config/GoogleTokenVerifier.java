package com.reactpf.reactpf.config;


import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

//구글 소셜로그인을 할떄 구글id 토큰을 검증하는 용도의 필터 클래스
@Configuration
public class GoogleTokenVerifier {
 private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";
 private final RestTemplate restTemplate = new RestTemplate();

 public String verify(String idToken) {
    try {
        //구글 서버에 토큰 검증 요청을해줌
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                                                                                GOOGLE_TOKEN_INFO_URL + idToken,
                                                                                HttpMethod.GET,
                                                                                null,
                                                                                new ParameterizedTypeReference<Map<String, Object>>() {}
                                                                            );
            if (response.getStatusCode() != HttpStatus.OK) {
                return null;
            }
            Map<String, Object> body = response.getBody();

            // 여기서 필요한 검증 (aud, iss 등) 추가 가능
            // 예: body.get("aud") 와 clientId 비교

            //사용자 정보 가져오기
            String email = (String)body.get("email");
            
            return email;
    } catch(Exception e) {
            return null;
    }
    

 }
}
