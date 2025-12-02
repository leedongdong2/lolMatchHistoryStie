package com.reactpf.reactpf.config;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactpf.reactpf.dto.RegionDto;


@Component
public class RiotWebClientConfig {


    public WebClient createRiotWebClient(RegionDto regionDto){
        return WebClient.builder()
                .baseUrl(String.format("https://%s.api.riotgames.com",regionDto.getRegion()))
                .defaultHeader("X-Riot-Token", "RGAPI-2b02a0e0-2807-4b49-8cf0-e2f573139960")
                .defaultHeader("USER_AGENT", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                .defaultHeader("ACCEPT_LANGUAGE", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .codecs(configurer -> configurer.defaultCodecs()
                .maxInMemorySize(10 * 1024 * 1024)) // 10MB   
                .build();
            }

    public WebClient createRiotWebClient(){
        return WebClient.builder()
                .baseUrl("https://kr.api.riotgames.com")
                .defaultHeader("X-Riot-Token", "RGAPI-2b02a0e0-2807-4b49-8cf0-e2f573139960")
                .defaultHeader("USER_AGENT", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                .defaultHeader("ACCEPT_LANGUAGE", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")    
                .build();
            }

    public WebClient createRiotDdragonWebClient() {
        return WebClient.builder()
               .baseUrl(String.format("https://ddragon.leagueoflegends.com/cdn"))
               .defaultHeader("ACCEPT_LANGUAGE", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .codecs(configurer -> configurer.defaultCodecs()
                .maxInMemorySize(16 * 1024 * 1024)) // 16MB 
               .build();
    }


        }
