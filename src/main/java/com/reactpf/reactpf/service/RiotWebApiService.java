package com.reactpf.reactpf.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.reactpf.reactpf.config.RiotWebClientConfig;
import com.reactpf.reactpf.dto.ChampionDto;
import com.reactpf.reactpf.dto.ChampionMasteryDto;
import com.reactpf.reactpf.dto.ItemsDto;
import com.reactpf.reactpf.dto.RankDto;
import com.reactpf.reactpf.dto.RegionDto;
import com.reactpf.reactpf.dto.RiotMatchDto;
import com.reactpf.reactpf.dto.RiotMatchTimeLineDto;
import com.reactpf.reactpf.dto.RiotUserDto;
import com.reactpf.reactpf.dto.RuneTreeDto;
import com.reactpf.reactpf.dto.SummonerSpellDto;

import reactor.core.publisher.Mono;

/**
 * webclient로 롤api,datadragonApi를 이용하여 롤에 대한 정보(매칭,아이템,챔피언등)를 알아오는 서비스 
 */
@Service
public class RiotWebApiService {
     
    @Autowired
    private RiotWebClientConfig riotWebClientConfig;
    
    /**
     * 
     * @param riotId
     * @param tag
     * @param region
     * @return
     * 아이디와 태그 접속 지역을 이용하여 해당 아이디 정보를 얻어온다
     *  해당 id의 puuid가 핵심
     * 
     */
    public RiotUserDto getPuuId(String riotId,String tag,String region){
        RegionDto regionDto =  new RegionDto();
        regionDto.setRegion(region);
        try {
                return riotWebClientConfig.createRiotWebClient(regionDto).get()
                    .uri("/riot/account/v1/accounts/by-riot-id/{riotId}/{tag}", riotId, tag)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientRespone->{
                            if(clientRespone.statusCode() == HttpStatus.NOT_FOUND) {
                                return Mono.error(new RuntimeException("없는 아이디입니다"));
                            }
                            return Mono.error(new RuntimeException("클라이언트에러 발생"));
                    })
                    .onStatus(status->status.is5xxServerError(), clientRespone->{
                        return Mono.error(new RuntimeException("서버오류 발생"));
                    })
                    .bodyToMono(RiotUserDto.class)
                    .block();
            } catch(RuntimeException e) {
                return null;
            }
     }
    
     /**
      * 
      * @param puuId
      * @param region
      * @param gameName
      * @param tag
      * @return
      * 해당 유저의 접속지역과 puuid를 이용하여 더 상세한 
        유저정보를 얻어온다(프로필id,레벨 등)
      */
     public RiotUserDto getRiotUserDto(String puuId,String region,String gameName,String tag){
        RegionDto regionDto =  new RegionDto();
        regionDto.setRegion(region);
        RiotUserDto riotUserDto = riotWebClientConfig.createRiotWebClient(regionDto).get()
                                  .uri("/lol/summoner/v4/summoners/by-puuid/{puuId}", puuId )
                                  .retrieve()
                                  .bodyToMono(RiotUserDto.class)
                                  .block();
        riotUserDto.setGameName(gameName);
        riotUserDto.setTagLine(tag);
        return riotUserDto;

    }

    /**
     * 
     * @param puuId
     * @param region
     * @return
     * 
     * 해당 유저의 랭크 티어 정보를 얻어온다
     */
    public RankDto getRiotUserRankDto(String puuId,String region){
        RegionDto regionDto =  new RegionDto();
        regionDto.setRegion(region);
        return riotWebClientConfig.createRiotWebClient(regionDto).get()
               .uri("/lol/league/v4/entries/by-puuid/{puuId}",puuId)
               .retrieve()
               .bodyToMono(new ParameterizedTypeReference<List<RankDto>>() {})
               .map(rankDto -> rankDto.isEmpty() ? null : rankDto.get(0))
               .block();
    }

    /**
     * 
     * @param puuId
     * @param region
     * @param matchType
     * @param matchStartIndex
     * @return
     * 필요한 매칭타입별(전체,랭크,칼바람 등)  게임매칭id를 얻어온다
     */
    public List<String> getRiotMatchId(String puuId,String region,String matchType,String matchStartIndex){
        RegionDto regionDto =  new RegionDto();
        regionDto.setRegion(region);
        return riotWebClientConfig.createRiotWebClient(regionDto).get()
               .uri("/lol/match/v5/matches/by-puuid/{puuId}/ids?queue={matchType}&start={matchStartIndex}&count=5",puuId,matchType,matchStartIndex)
               .retrieve()
               .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
               .block();
    }

    //게임매칭 id에 해당하는 게임내 상세정보(게임지속시간,게임종료,게임속 유저들의 정보(kda,골드,cs등))들을 얻어온다
    public RiotMatchDto getRiotMatchInfo(String matchId,String region){
        RegionDto regionDto =  new RegionDto();
        regionDto.setRegion(region);
        return riotWebClientConfig.createRiotWebClient(regionDto).get()
               .uri("/lol/match/v5/matches/{matchId}",matchId)
               .retrieve()
               .bodyToMono(RiotMatchDto.class)
               .block();
    }

    //게임매칭 id에 해당하는 게임 내 타임라인 이벤트 정보(유저들의 아이템 구매 이벤트,스킬레벨업 이벤트 등)들을 얻어온다
    public RiotMatchTimeLineDto getRiotMatchTimeLineDto(String matchId,String region) {
        RegionDto regionDto = new RegionDto();
        regionDto.setRegion(region);
        return riotWebClientConfig.createRiotWebClient(regionDto).get()
               .uri("/lol/match/v5/matches/{matchId}/timeline",matchId)
               .retrieve()
               .bodyToMono(RiotMatchTimeLineDto.class)
               .block();
    }

    //해당 유저의 챔피언 숙련도 상위4개의 정보(숙련도레벨,챔피언이름 등)를 얻어온다
    public List<ChampionMasteryDto> getChampionMasteryDto(String puuId,String region){
        RegionDto regionDto =  new RegionDto();
        regionDto.setRegion(region);
        return riotWebClientConfig.createRiotWebClient(regionDto).get()
               .uri("/lol/champion-mastery/v4/champion-masteries/by-puuid/{puuId}/top?count=4",puuId)
               .retrieve()
               .bodyToMono(new ParameterizedTypeReference<List<ChampionMasteryDto>>() {})
               .block();
    }

    // 롤 로테이션 챔피언들의 아이디를 얻어온다
    public List<String> getRotationsChampionIds() {
         
        JsonNode rootNode = riotWebClientConfig.createRiotWebClient().get() //json 데이터 응답을 JsonNode파싱해서 받아온다
                            .uri("/lol/platform/v3/champion-rotations")
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .block();
        
        if (rootNode == null || !rootNode.has("freeChampionIds")) {
                return Collections.emptyList();
            }

        List<String> rotationChampionsIds  = new ArrayList<>();
        
        //로테이션 챔피언들의 이름의 배열을 꺼내 따로 list로 담아준다 
        for(JsonNode idNode : rootNode.get("freeChampionIds")) {
            rotationChampionsIds.add(idNode.asText());
        }

        return rotationChampionsIds;
        
    }
    
    //datadragon에서 제공하는 스펠 정보(스펠 이름,스펠 효과 등)들을 통채로 얻어온다
    public SummonerSpellDto getSummonerSpellDto() {
            return riotWebClientConfig.createRiotDdragonWebClient().get()
            .uri("/15.8.1/data/ko_KR/summoner.json")
            .retrieve()
            .bodyToMono(SummonerSpellDto.class)
            .block();
    }
    
    
    ///datadragon에서 제공하는 챔피언들의 정보(스킬 정보,챔피언 이름 등)들을 통채로 얻어온다
    public ChampionDto getChampionDto() {
        return riotWebClientConfig.createRiotDdragonWebClient().get()   
                .uri("/15.20.1/data/ko_KR/champion.json")
                .retrieve()
                .bodyToMono(ChampionDto.class)
                .block();
    }


    //datadragon에서 제공하는 룬정보(룬이름,룬 효과 등)들을 통채로 얻어온다
    public List<RuneTreeDto> getRuneTreeDto() {
        return riotWebClientConfig.createRiotDdragonWebClient().get()
               .uri("/15.14.1/data/ko_KR/runesReforged.json")
               .retrieve()
               .bodyToMono(new ParameterizedTypeReference<List<RuneTreeDto>>() {})
               .block();
    }
    
    //datadragon에서 제공하는 아이템정보(아이템이름,아이템효과 등) 들을 통채로 얻어온다
    public ItemsDto getItemDto() {
        return riotWebClientConfig.createRiotDdragonWebClient().get()
               .uri("/15.15.1/data/ko_KR/item.json")
               .retrieve()
               .bodyToMono(ItemsDto.class)
               .block();
    }



}

