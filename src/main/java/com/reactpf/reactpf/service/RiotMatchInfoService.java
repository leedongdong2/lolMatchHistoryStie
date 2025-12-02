package com.reactpf.reactpf.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Service;

import com.reactpf.reactpf.dto.ChampionDto;
import com.reactpf.reactpf.dto.ChampionMasteryDto;
import com.reactpf.reactpf.dto.GoldAndCsDto;
import com.reactpf.reactpf.dto.ItemsDto;
import com.reactpf.reactpf.dto.RiotMatchDto;
import com.reactpf.reactpf.dto.RiotMatchDto.Participant;
import com.reactpf.reactpf.dto.RiotMatchDto.SummonerSpell;
import com.reactpf.reactpf.dto.RiotMatchDto.TeamDto;
import com.reactpf.reactpf.dto.RiotMatchTimeLineDto;
import com.reactpf.reactpf.dto.RiotMatchTimeLineDto.ParticipantFrameDTO;
import com.reactpf.reactpf.dto.RuneTreeDto;
import com.reactpf.reactpf.dto.SummonerSpellDto;

@Service
public class RiotMatchInfoService {

    private final RiotWebApiService riotWebApiService;

    public RiotMatchInfoService(RiotWebApiService riotWebApiService) {
        this.riotWebApiService = riotWebApiService;
    }
    
    /*
     * 롤api에서 가져온 숙련도 챔피언들의 정보중 id를 비교하여 s
     * datadragon에서 가져온 챔피언정보를 바탕으로 챔피언 이름,마지막 플레이 날짜를 알아낸다  
     */
    public List<ChampionMasteryDto> getChamponMasteryNames(List<ChampionMasteryDto> list,ChampionDto championDto){
            for(ChampionMasteryDto mastery : list){ 
            mastery.setChampionName(getChampionName(championDto, String.valueOf(mastery.getChampionId())));
            mastery.setChampionKeyName(getChampionKeyName(championDto,String.valueOf(mastery.getChampionId())));
            mastery.setLastPlayTimeFormat(Instant.ofEpochMilli(mastery.getLastPlayTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString());
            }
        return list;
    }

    /*
     * 매치id로 매치dto불러오기
     * 
     */
    public List<RiotMatchDto> getRiotMatchDtos(List<String> riotMatchIdList,String region,String riotTag) {
            List<RiotMatchDto> riotMatchDtoList = new ArrayList<>();

            for(String riotMatchId : riotMatchIdList) {
                RiotMatchDto riotMatchDto = riotWebApiService.getRiotMatchInfo(riotMatchId, region);
                riotMatchDtoList.add(riotMatchDto);
            }

            return riotMatchDtoList;
    }

    //게임 분류 가져오기(랭크,칼바람)
    public List<RiotMatchDto> getRiotMatchDtoQueueName(List<RiotMatchDto> riotMatchDtoList) {
        
        for(RiotMatchDto riotMatchDto : riotMatchDtoList) {
            riotMatchDto.getInfo().setQueueName(getQueueName(riotMatchDto.getInfo().getQueueId()));
        }
        
        return riotMatchDtoList;
    }

    //피들스틱 표기오류로 이름 정정
    public List<RiotMatchDto> changeRiotChampionName(List<RiotMatchDto> riotMatchDtoList) {

        for( RiotMatchDto riotMatchDto : riotMatchDtoList ) {
            changeRiotChampionName(riotMatchDto);
        }
        return riotMatchDtoList;
    }

    public RiotMatchDto changeRiotChampionName( RiotMatchDto riotMatchDto) {

            for( RiotMatchDto.Participant participant : riotMatchDto.getInfo().getParticipants() ) {
                    if( participant.getChampionName().equals("FiddleSticks") ) {
                            participant.setChampionName("Fiddlesticks");
                    } 
            }
        return riotMatchDto;
    }

    //매칭정보중 검색한 id의 정보만 뽑아오기
    public List<RiotMatchDto> getSearchIdParticipant(List<RiotMatchDto> riotMatchDtoList,String riotId,String riotTag) {

        for( RiotMatchDto riotMatchDto : riotMatchDtoList ) {
            getSearchIdParticipant(riotMatchDto, riotId, riotTag);
        }
            
        return riotMatchDtoList;
    }

    public RiotMatchDto getSearchIdParticipant(RiotMatchDto riotMatchDto,String riotId,String riotTag) {

            for( RiotMatchDto.Participant participant : riotMatchDto.getInfo().getParticipants() ) {
                    if(participant.getRiotIdGameName().equals(riotId) && participant.getRiotIdTagline().equals(riotTag)) {
                            riotMatchDto.getInfo().setSerchIdParticipant(participant);
                    }
            }
            
        return riotMatchDto;
    }
    
    //매칭정보중 검색한 id의 팀(블루,레드) 결과(승,패)  한국말로 다시 저장
    public List<RiotMatchDto> setSearchIdTeamAndWin(List<RiotMatchDto> riotMatchDtoList) {

        for( RiotMatchDto riotMatchDto : riotMatchDtoList ) {
             setSearchIdTeamAndWin(riotMatchDto);
        }
        return riotMatchDtoList;
    }


    public RiotMatchDto setSearchIdTeamAndWin(RiotMatchDto riotMatchDto) {

            if( riotMatchDto.getInfo().getSerchIdParticipant().getTeamId() == 100 ) {
                riotMatchDto.getInfo().setSerchIdTeam("블루팀");
                riotMatchDto.getInfo().setNonSerchIdTeam("레드팀");
            } else {
                riotMatchDto.getInfo().setSerchIdTeam("레드팀");
                riotMatchDto.getInfo().setNonSerchIdTeam("블루팀");
            }
            if( riotMatchDto.getInfo().getSerchIdParticipant().isWin() == true ) {
                riotMatchDto.getInfo().setSerchIdWin("승리");
                riotMatchDto.getInfo().setNonSerchIdWin("패배");
            } else {
                riotMatchDto.getInfo().setSerchIdWin("패배");
                riotMatchDto.getInfo().setNonSerchIdWin("승리");
            }

        return riotMatchDto;
    }

    //매칭정보중 검색한 id의 룬id로 룬정보를 얻어 다시 저장
    public List<RiotMatchDto> setSearchIdRuneInfo(List<RiotMatchDto> riotMatchDtoList,List<RuneTreeDto> runeTreeDto){
        
        for( RiotMatchDto riotMatchDto : riotMatchDtoList ) {
                setSearchIdRuneInfo(riotMatchDto, runeTreeDto);
        }

        return riotMatchDtoList;
    }

    public RiotMatchDto setSearchIdRuneInfo(RiotMatchDto riotMatchDto,List<RuneTreeDto> runeTreeDto){

        for(RiotMatchDto.PerkStyleDto perkStyleDto : riotMatchDto.getInfo().getSerchIdParticipant().getPerks().getStyles()) {
                for(RiotMatchDto.PerkStyleSelectionDto perkStyleSelectionDto : perkStyleDto.getSelections()) {
                        RiotMatchDto.Rune searchIdRune = findRuneById(runeTreeDto, perkStyleSelectionDto.getPerk());
                        riotMatchDto.getInfo().getSerchIdParticipant().getRune().add(searchIdRune);
                }
        }

        return riotMatchDto;
    }

    public RiotMatchDto setMatchParticipantsRuneInfo(RiotMatchDto riotMatchDto,List<RuneTreeDto> runeTreeDto) {
        
        for(RiotMatchDto.Participant participant : riotMatchDto.getInfo().getParticipants()) {
            for(RiotMatchDto.PerkStyleDto perkStyleDto : participant.getPerks().getStyles()) {
                    for(RiotMatchDto.PerkStyleSelectionDto perkStyleSelectionDto : perkStyleDto.getSelections()) {
                            RiotMatchDto.Rune searchIdRune = findRuneById(runeTreeDto, perkStyleSelectionDto.getPerk());
                            participant.getRune().add(searchIdRune);
                    }
            }
        }
        return riotMatchDto;
    }


    //매칭정보중 검색한 id의 아이템리스트 번호로 아이템정보를 얻어 다시저장
    public List<RiotMatchDto> setSearchIdItemInfo(List<RiotMatchDto> riotMatchDtoList,ItemsDto itemsDto) {

        for( RiotMatchDto riotMatchDto : riotMatchDtoList ) {
                setSearchIdItemInfo(riotMatchDto, itemsDto);
        }

        return riotMatchDtoList;
    }

    public RiotMatchDto setSearchIdItemInfo(RiotMatchDto riotMatchDto,ItemsDto itemsDto) {

            List<Integer> items = riotMatchDto.getInfo().getSerchIdParticipant().getItemList();
                for(Integer itemKey : items) {
                        RiotMatchDto.Item  item = convertToRiotMatchItem(itemsDto, String.valueOf(itemKey));
                        riotMatchDto.getInfo().getSerchIdParticipant().getItems().add(item);
                }

        return riotMatchDto;
    }

    public RiotMatchDto setMatchParticipantsItemInfo(RiotMatchDto riotMatchDto,ItemsDto itemsDto) {
        
        for(RiotMatchDto.Participant participant : riotMatchDto.getInfo().getParticipants()) {
            List<Integer> items = participant.getItemList();
            for(Integer itemKey : items) {
                    RiotMatchDto.Item  item = convertToRiotMatchItem(itemsDto, String.valueOf(itemKey));
                    participant.getItems().add(item);
            }
        }
        return riotMatchDto;
    }

    


    //매칭정보중 검색한 id의 스펠 번호로 스펠정보를 얻어 다시 저장
    public List<RiotMatchDto> setSearchIdSpellInfo(List<RiotMatchDto> riotMatchDtoList,SummonerSpellDto summonerSpellDto) {
    
        for(RiotMatchDto riotMatchDto : riotMatchDtoList) {
                setSearchIdSpellInfo(riotMatchDto, summonerSpellDto);
        }

        return riotMatchDtoList;
    }

    public RiotMatchDto setSearchIdSpellInfo(RiotMatchDto riotMatchDto,SummonerSpellDto summonerSpellDto) {
    
            riotMatchDto.getInfo().getSerchIdParticipant().getSummonerSpells().add(getSummonerSpellInfo(summonerSpellDto, String.valueOf(riotMatchDto.getInfo().getSerchIdParticipant().getSummoner1Id())));
            riotMatchDto.getInfo().getSerchIdParticipant().getSummonerSpells().add(getSummonerSpellInfo(summonerSpellDto, String.valueOf(riotMatchDto.getInfo().getSerchIdParticipant().getSummoner2Id())));

        return riotMatchDto;
    }

    public RiotMatchDto setMatchParticipantsSpellInfo(RiotMatchDto riotMatchDto,SummonerSpellDto summonerSpellDto) {
            for(RiotMatchDto.Participant participant : riotMatchDto.getInfo().getParticipants()) {
                participant.getSummonerSpells().add(getSummonerSpellInfo(summonerSpellDto, String.valueOf(riotMatchDto.getInfo().getSerchIdParticipant().getSummoner1Id())));
                participant.getSummonerSpells().add(getSummonerSpellInfo(summonerSpellDto, String.valueOf(riotMatchDto.getInfo().getSerchIdParticipant().getSummoner2Id())));
            }
        return riotMatchDto;
    }

    //게임지속시간 종료 날짜를 분 초 / 프리티타임으로 변환하여 다시 저장 
    public List<RiotMatchDto> setGameDurationAndendDate(List<RiotMatchDto> riotMatchDtoList) {
        
        PrettyTime p = new PrettyTime();

        for(RiotMatchDto riotMatchDto : riotMatchDtoList) {
            long gameDuration = riotMatchDto.getInfo().getGameDuration();
            Duration duration = Duration.ofSeconds(gameDuration);
            riotMatchDto.getInfo().setGameDurationMinutes(duration.toMinutes());
            riotMatchDto.getInfo().setGameDurationSeconds(duration.minusMinutes(duration.toMinutes()).getSeconds());

            Date date = new Date(riotMatchDto.getInfo().getGameEndTimestamp());
            riotMatchDto.getInfo().setGameEndTimestampPrettyTime(p.format(date));
        }

        return riotMatchDtoList;
    }

    //팀 id로 검색한id와 같은팀원 아닌 팀원을 따로 나누기
    public RiotMatchDto groupByTeamSearchId(RiotMatchDto riotMatchDto,int serchedIdTeamId) {

        for(RiotMatchDto.Participant participant : riotMatchDto.getInfo().getParticipants()){
            if(participant.getTeamId() == serchedIdTeamId) {
                riotMatchDto.getInfo().getSerchIdTeamParticipants().add(participant);
            } else {
                riotMatchDto.getInfo().getNonSerchIdTeamParticipants().add(participant);
            }

        }

        return riotMatchDto;
    }

    //팀원별 골드를 통한 팀별 골드 합산 
    public RiotMatchDto calculateTotalGoldPerTeam(RiotMatchDto riotMatchDto){

            int blueTeamTotalGold = 0;
            int redTeamTotalGold = 0;

            for(RiotMatchDto.Participant participant : riotMatchDto.getInfo().getParticipants()){
                if(participant.getTeamId() == 100) {
                    blueTeamTotalGold = blueTeamTotalGold + participant.getGoldEarned();
                } else { 
                    redTeamTotalGold = redTeamTotalGold + participant.getGoldEarned();
                }
            }

            riotMatchDto.getInfo().getTeams().get(0).setTeamTotalGold(blueTeamTotalGold);
            riotMatchDto.getInfo().getTeams().get(1).setTeamTotalGold(redTeamTotalGold); 

            return riotMatchDto;      
    }

    //kad비율 구하기
    public List<Participant> calculateKdaForParticipants(List<Participant> participants) {

        for (Participant participant : participants){
            int k = participant.getKills();
            int d = participant.getDeaths();
            int a = participant.getAssists();

            double kda;

            if(d == 0) {
                kda = k + a;

                participant.setKda(kda);
            } else {
                kda = ((double)(k+a))/d;
                
                BigDecimal db = new BigDecimal(String.valueOf(kda)).setScale(2, RoundingMode.HALF_UP);
                kda = db.doubleValue();

                participant.setKda(kda);
            }
        }

        return participants;
    }

    //팀 총 킬수 구하기
    public int calculateTotalTeamKills(List<Participant> participants) {
        int totalTeamKills = 0;

        for(Participant participant : participants) {
            totalTeamKills = totalTeamKills + participant.getKills();
        }

        return totalTeamKills;
    }

    //팀에서 킬 관여율 구하기
    public List<Participant> calculateKPForParticipants(List<Participant> participants) {
        
        int totalTeamKills = calculateTotalTeamKills(participants);

        for(Participant participant : participants) {
            int k = participant.getKills();
            int a = participant.getAssists();

            double KP = ((double)(k + a) / totalTeamKills) * 100;

            BigDecimal db = new BigDecimal(String.valueOf(KP)).setScale(0,RoundingMode.HALF_UP);//소숫점과 반올림 세팅
            participant.setKdaPercent(db.intValue()); //int타입으로 파싱하여 넣어줌
        }

        return participants;
    }

    //챔피언에게 입힌 피해량 퍼센트 구하기
    public List<Participant> calculateMaxDamagePercentForParticipants(List<Participant> participants) {

        int teamMaxDamage = 0;

        for(Participant participant : participants ) {
            if( teamMaxDamage < participant.getTotalDamageDealtToChampions() ) {
                teamMaxDamage = participant.getTotalDamageDealtToChampions();
            }
        }

        for(Participant participant : participants) {
            int playerDamage = participant.getTotalDamageDealtToChampions();
            double totalDamageDealtToChampionsPercent = ((double) playerDamage/teamMaxDamage) * 100;
            BigDecimal bd = new BigDecimal(totalDamageDealtToChampionsPercent).setScale(0,RoundingMode.HALF_UP);
            participant.setTotalDamageDealtToChampionsPercent(bd.intValue());
        }

        return participants;
    }

    //받은피해량 퍼센트 구하기
    public List<Participant> calculateMaxTakenPercentForParticipants(List<Participant> participants) {
        
        int teamMaxTaken = 0;

        for(Participant participant : participants) {
            if(teamMaxTaken < participant.getTotalDamageTaken()) {
                teamMaxTaken = participant.getTotalDamageTaken();
            }
        }

        for(Participant participant : participants){
            int playerTaken = participant.getTotalDamageTaken();
            double totalDamageTakenPercent = ((double) playerTaken/teamMaxTaken) *100;
            BigDecimal bd = new BigDecimal(totalDamageTakenPercent).setScale(0,RoundingMode.HALF_UP);
            participant.setTotalDamageTakenPercent(bd.intValue());
        }


        return participants;
    }

    public List<Participant> calculateCSPMForParticipants(List<Participant> participants,long gameDuration){

        Duration duration = Duration.ofSeconds(gameDuration);
        long roundedMiniutes = Math.round(duration.getSeconds()/60.0);

        for(Participant participant : participants) {
            int totalCs = participant.getTotalMinionsKilled() + participant.getNeutralMinionsKilled();
            double cspm = (double)totalCs/roundedMiniutes;
            BigDecimal bd = new BigDecimal(String.valueOf(cspm)).setScale(1,RoundingMode.HALF_UP);
            participant.setCspm(bd.doubleValue());
        }

        return participants;
    }

    //전체 킬중 팀별 킬퍼센트 구하기
    public RiotMatchDto calculateKpPerTeam(RiotMatchDto riotMatchDto){
        
        int searchIdTeamKills = calculateTotalTeamKills(riotMatchDto.getInfo().getSerchIdTeamParticipants());
        int nonSearchIdTeamKills = calculateTotalTeamKills(riotMatchDto.getInfo().getNonSerchIdTeamParticipants());

        int totTeamkills = searchIdTeamKills + nonSearchIdTeamKills;

        for (TeamDto team : riotMatchDto.getInfo().getTeams()) {

            if ( riotMatchDto.getInfo().getSerchIdParticipant().getTeamId() == team.getTeamId() ) {
                    team.getObjectives().getChampion().setKills(searchIdTeamKills);
                    double teamKillPercent = ((double)searchIdTeamKills/totTeamkills) * 100;
                    BigDecimal bd = new BigDecimal(teamKillPercent).setScale(0,RoundingMode.HALF_UP);
                    team.setTeamKillPercent(bd.intValue());
            } else {
                    team.getObjectives().getChampion().setKills(nonSearchIdTeamKills);
                    double teamKillPercent = ((double)nonSearchIdTeamKills/totTeamkills) * 100;
                    BigDecimal bd = new BigDecimal(teamKillPercent).setScale(0,RoundingMode.HALF_UP);
                    team.setTeamKillPercent(bd.intValue());
            }

        }

        return riotMatchDto;
    }

    

    //팀별 얻은골드량 퍼센트  구하기
    public RiotMatchDto calculateTotalGoldPercentPerTeam(RiotMatchDto riotMatchDto){
        
        int totTeamGold = riotMatchDto.getInfo().getTeams().get(0).getTeamTotalGold() + riotMatchDto.getInfo().getTeams().get(1).getTeamTotalGold();
        
        for(TeamDto team : riotMatchDto.getInfo().getTeams()) {
              int teamGold = team.getTeamTotalGold();
              double teamGoldPercent = ((double) teamGold/totTeamGold) * 100;
              BigDecimal bd = new BigDecimal(teamGoldPercent).setScale(0,RoundingMode.HALF_UP);
              team.setTeamTotalGoldPercent(bd.intValue());
        }

        return riotMatchDto;
    }
        
    //해당하는 스펠id와 동일한 key값을 가진 스펠 정보를 얻어와 riotMatchDto안의 summonerSpellDto로 변환
    public SummonerSpell getSummonerSpellInfo(SummonerSpellDto summonerSpellDto,String summonerSpellId) {
        
        if (summonerSpellDto == null || summonerSpellId == null) {
            return null;
        }
        else {
            //summonerSpellDto의 data부분만 추출
            Map<String,SummonerSpellDto.SummonerSpell> spellMap = summonerSpellDto.getData();
           
           return spellMap.values().stream()
                    .filter(SummonerSpell -> SummonerSpell.getKey().equals(summonerSpellId)) // SummonerSpellDto.SummonerSpell 중 summonerSpellId와 key값이 같은 스펠정보를 추출
                    .findFirst()
                    .map(this::convertToSummonerSpell)//riotMatchDto.summonerSpellDto로 변환시켜줌
                    .orElse(null);
        }
    }

    // SummonerSpellDto.SummonerSpell 값을 riotMatchDto.summonerSpellDto에 입력해 변환시켜준다
    public SummonerSpell convertToSummonerSpell(SummonerSpellDto.SummonerSpell summonerSpell) {
            SummonerSpell spell = new RiotMatchDto.SummonerSpell();
            spell.setSummonerSpellId(summonerSpell.getId());
            spell.setSummonerSpellName(summonerSpell.getName());
            spell.setSummonerDescription(summonerSpell.getDescription());
            return spell;
     }

     //해당 챔피언id에 해당하는 챔피언 이름을 반환해준다
    public String getChampionName(ChampionDto championDto,String championId) {
        
        if (championDto == null || championId == null) {
            return "챔피언 데이터가 로드되지 않았습니다.";
        }
        else {
            //chapionDto에서 챔피언 data 부분만 꺼내줌(key:챔피언이름,value:champion 객체)
            Map<String,ChampionDto.Champion> champMap = championDto.getData();
                   //map의 벨류들의 컬렉션을 반환해서 스트림으로 순차적으로 처리할수 있게 해준다 (for(String id : id) )
           return champMap.values().stream()
                    .filter(champion -> champion.getKey().equals(championId)) //챔피언 id와 동일한 key를 가진 챔피언 객체를 찾아온다//for문 안의 if랑 비슷 조건이 맞는 데이터들을 넘겨서 새 스트림을 생성해줌
                    .map(ChampionDto.Champion::getName)//(champion->champion.getName)해당 객체중 이름만 추출해준다 map은 데이터를 다른 형태로 변환하는 필드를 원하는데로 추출하거나 계산결과를 바꾸는것이다. 여기선 필터에서 남겨진 객체들중 이름만 뽑아오는데 사용햇다
                    .findFirst()//남겨지고 변환한 데이터들 중 첫번쨰꺼를 반환해오는 것                                                                        
                    .orElse("챔피언 io를 찾을수 없습니다"); // 값이 없을떄 기본값 저장
        }
    }

    //해당 챔피언id에 해당하는 챔피언 key를 반환해준다
    public String getChampionKeyName(ChampionDto championDto,String championId) {
        
        if (championDto == null || championId == null) {
            return "챔피언 데이터가 로드되지 않았습니다.";
        }
        else {
            Map<String,ChampionDto.Champion> champMap = championDto.getData();
           
           return champMap.values().stream()
                    .filter(champion -> champion.getKey().equals(championId)) //챔피언 id와 동일한 key를 가진 챔피언 객체를 찾아온다
                    .map(ChampionDto.Champion::getId)//이미지를 위한 챔피언id만 가져와준다
                    .findFirst()
                    .orElse("챔피언 io를 찾을수 없습니다");
        }
    }


    //해당 챔피언id에 해당하는 챔피언 key를 반환해준다
    public List<ChampionDto.Champion> getTagChampion(ChampionDto championDto,String championTag) {
        
                                                    //getData는 map 데이터 구조 키와 벨류값이 둘다 필요하므로 entryset으로 키와 벨류를 모두 가져와 스트림해준다
             List<ChampionDto.Champion> champions = championDto.getData().entrySet().stream()
                                                    .sorted(Map.Entry.comparingByKey())//가져온 엔트리를 키 순서대로 정렬해준다
                                                    .map(entry -> (ChampionDto.Champion) entry.getValue())//챔피언 객체의 벨류만 가져온다
                                                    .filter(champion -> { 
                                                        if(championTag == null || championTag.trim().isEmpty()) {//championTag가null이거나 비어잇으면 모든 챔피언을 남김
                                                            return true;
                                                        }                                                      //   contains : 어떤 문자가 문자열에 들어잇는지 확인할수잇다 list나 다른 세트에서는 값이 포함어있는지 체크가능          
                                                        return champion.getTags() != null && champion.getTags().contains(championTag);//championTag가 존재하면 getTag에서 championTag가 맞는 애들만 남김
                                                    })
                                                    .collect(Collectors.toList()); // 조건에 맞는 객체들을 리스트로 수집해 반환

            return champions;

    }


    //queueId를 해당하는 id와 맞는 QueueName을 반환해준다 
    public String getQueueName(int queueId){
        switch (queueId) {
            case 420 : 
                return "솔로/듀오 랭크";
            case 430 :
                return "일반";
            case 440 :
                return "자유랭크";
            case 450 :
                return "칼바람 나락";
            case 700 :
                return "URF모드";
            default:
                return"기타 모드";
        }
    }


    //해당 유저의 아이템 구매 이벤트를 4분 간격으로 그룹화 한다
    public  Map<Integer, List<RiotMatchTimeLineDto.EventDTO>> getItemPurchasesEvery4Minutes(RiotMatchTimeLineDto dto, int participantId) {
         int interval = 240000;//4분 시간 인터벌 설정
                //matchTimeLineDto 내 모든 프레임과 이벤트를 순회
         return dto.getInfo().getFrames().stream()
        .flatMap(frame -> frame.getEvents().stream())
        .filter(event -> event.getParticipantId() != null && event.getParticipantId() == participantId) // 해당 유저의 이벤트만 추출
        .filter(event -> "ITEM_PURCHASED".equals(event.getType())) // 아이템 구매 이벤트만 추출
        .distinct()  // 중복 제거
        .collect(Collectors.groupingBy(event -> (int) (event.getTimestamp() / interval))); //이벤트 발생 시간을 4분간격으로 나누어 그룹핑
    }

    //해당 유저의 스킬레벨업 이벤트를 list로 반환
    public List<RiotMatchTimeLineDto.EventDTO> getSkillLevelUp(RiotMatchTimeLineDto dto,int participantId){
            return dto.getInfo().getFrames().stream()
            .flatMap(frame -> frame.getEvents().stream())
            .filter(event -> event.getParticipantId() != null  && event.getParticipantId() == participantId) // 해당 유저의 이벤트만 추출
            .filter(event -> "SKILL_LEVEL_UP".equals(event.getType())) // 스킬레벨업 이벤트만 추출
            .distinct()
            .collect(Collectors.toList());  //스킬레벨업부분의 eventDto만 list화 해서 반환
    }

    // 해당 유저의 골드와 CS(미니언 + 정글 몬스터 킬 수)를  4분 단위로 그룹화 해서 반환
    public Map<Integer, GoldAndCsDto> getGoldAndCsEvery4Minutes(RiotMatchTimeLineDto dto, int participantId) {
    int interval = 240000;

    return dto.getInfo().getFrames().stream()
        .filter(frame -> frame.getParticipantFrames() != null &&
                         frame.getParticipantFrames().values().stream()
                               .anyMatch(p -> p.getParticipantId() == participantId))// 해당 유저가 존재하는 프레임만 반환받아와준다
        .collect(Collectors.toMap( // Map<Integer, GoldAndCsDto> 형식으로 변환
            frame -> frame.getTimestamp() / interval, //key: 4분 단위로 묶기 0,0,0,(0~4) 1,1,1(4~8)이런식으로 묶인다
            frame -> { // value: 해당 프레임에서 해당 참가자의 골드와 CS 정보를 담은 DTO 생성
                ParticipantFrameDTO pDto = frame.getParticipantFrames().values().stream() 
                    .filter(p -> p.getParticipantId() == participantId) // 현재 프레임의 해당 유저의 정보를 찾음
                    .findFirst()
                    .orElse(null);
                
                //예외 방지 null값으로 받아와지면 강제로 0을 주입
                if (pDto == null) return new GoldAndCsDto(0, 0);

                //누적골드
                int totalGold = pDto.getTotalGold();
                //미니언 총합
                int cs = pDto.getMinionsKilled() + pDto.getJungleMinionsKilled();
                //GoldAndCsDto에 값을 넣어 변환
                return new GoldAndCsDto(totalGold, cs);
            },
            (existing, replacement) -> replacement // 타임스탬프 중복 시 최신값으로 만약 0,0,0의 키를 가진 값들이 나올경우 가장 나중의 키의 벨류를 선택함 그렇게 계속 하다보면 
                                                   // 0,0,0,1,1,1,2,2,2, 이런식의 key들이 4분 단위로 0,1,2 이런식으로 같은 키들의 최신값의 값들만 남는다
         ));
    }

    //해당 유저의 룬id에 해당하는 룬객체를 찾아 RiotMatchDto.Rune로 변환 후 반환
    public RiotMatchDto.Rune findRuneById(List<RuneTreeDto> runeTrees, int targetId) {
    return runeTrees.stream()//룬트리를 순회
            .flatMap(tree -> tree.getSlots().stream())//.flatMap 각 슬롯 스트림을 하나의 평면 스트림으로 합친다 List<RuneTreeDto>의 RuneTreeDto들에 슬롯이 List<Slot> 또 리스트로 되어잇는데 이걸 각각이 아니라 합쳐서 사용해야하기 떄문에  
            .flatMap(slot -> slot.getRunes().stream())//map으로 만들어 주는것이 아니라 flatMap을 사용해서 list<list<slot>>에서 그냥 list<slot>으로 만들어 준다<- 평면적으로 이어준다 getRune도 동일 
            .filter(rune -> rune.getId() == targetId) //id가 일치하는 룬을 필터링
            .findFirst()//가장 먼저 일치하는 룬 하나 선택
            //.map(rune -> this.convertToRiotMatchRune(rune)현재 클래스의 인스턴스에 rune매개를 받아 변환메소드를 통해 변환 그대로 반환)
            .map(this::convertToRiotMatchRune) //찾은 룬을 RiotMatchDto.Rune로 변환 //map은 스트림이 될떄 list<list<slot>>이 있다면 list<slot>을 각각 필터를 건다 구조는 list<list<slot>>그대로 유지됨
            .orElse(null);
    }

    //RuneTreeDto.Rune rune을 RiotMatchDto.Rune에 값을 넣어 변환 후 반환 
    private RiotMatchDto.Rune convertToRiotMatchRune(RuneTreeDto.Rune rune) {
    RiotMatchDto.Rune converted = new RiotMatchDto.Rune();
    converted.setId(rune.getId());
    converted.setName(rune.getName());
    converted.setKey(rune.getKey());
    converted.setIcon(rune.getIcon());
    converted.setLongDesc(rune.getLongDesc());
    converted.setShortDesc(rune.getShortDesc());
    converted.setTooltip(rune.getTooltip());
    return converted;
}

//해당 유저의 itemid에 해당하는 아이템 객체를 찾아 RiotMatchDto.Item로 변환 후 반환
public RiotMatchDto.Item convertToRiotMatchItem(ItemsDto items,String itemId){
    RiotMatchDto.Item  item = new RiotMatchDto.Item();

    if(itemId == null || itemId.isEmpty() || itemId.isBlank() || itemId.equals("0")){
        item.setName(" ");
        item.setDescription(" ");
    } else {
        item.setName(items.getData().get(itemId).getName());
        item.setDescription(items.getData().get(itemId).getDescription());
    }
    return item;
}

    //4분 주기로 뽑아낸 아이템 구매 이벤트 객체에 아이템 이름과 설명을 넣어준다
    public Map<Integer, List<RiotMatchTimeLineDto.EventDTO>> setItemInfo(Map<Integer, List<RiotMatchTimeLineDto.EventDTO>> eventDtos,ItemsDto itemsDto) {

         for(Map.Entry<Integer,List<RiotMatchTimeLineDto.EventDTO>> event : eventDtos.entrySet()) {
            for( RiotMatchTimeLineDto.EventDTO itemEvent : event.getValue() ) {
                itemEvent.setItemName(itemsDto.getData().get(String.valueOf(itemEvent.getItemId())).getName()); 
                itemEvent.setItemDescription(itemsDto.getData().get(String.valueOf(itemEvent.getItemId())).getDescription());
            };
        }

        return eventDtos;
    }

    //골드 및 cs데이터를 차트에 넣는값으로  변환해줌
    public Map<String,List<Integer>> getGoldAndCsChartData(Map<Integer, GoldAndCsDto> goldAndCs) {

            List<Integer> intervals = new ArrayList<>();
            List<Integer> goldList = new ArrayList<>();
            List<Integer> csList = new ArrayList<>();


          for (Map.Entry<Integer, GoldAndCsDto> entry : goldAndCs.entrySet()) {
                intervals.add(entry.getKey()); //4분 주기 시간 구간
                goldList.add(entry.getValue().getTotalGold());//4분당 누적 골드
                csList.add(entry.getValue().getCs());// 4분당 cs
            }

            Map<String,List<Integer>> goldAndCsChartData = new HashMap<>();

            goldAndCsChartData.put("intervals", intervals);
            goldAndCsChartData.put("goldList", goldList);
            goldAndCsChartData.put("csList",csList);

            return goldAndCsChartData;
    }

    public int setWinAndLoseCount(String win,List<RiotMatchDto> riotMatchDtoList) {
            
            int winCount = 0;

            for(RiotMatchDto riotMatchDto : riotMatchDtoList) {
                if( riotMatchDto.getInfo().getSerchIdWin().equals(win)) {
                    winCount = winCount + 1;
            }
            
        }
        return winCount;
    }

    public int calculateLineCountPercent(int lineCount,int gameCount) {
        
        if(lineCount == 0 ){
            return 0;
        } else {
            double lineCountPercent = (lineCount/ (double)(gameCount)) * 100 ;
            BigDecimal bd = new BigDecimal(String.valueOf(lineCountPercent)).setScale(0,RoundingMode.HALF_UP);

            return bd.intValue();
        }
    }

    
    public int calculateLineCount(String line,List<RiotMatchDto> riotMatchDtoList) {
        
        int lineCount = 0;

        for(RiotMatchDto riotMatchDto : riotMatchDtoList) {
            if(riotMatchDto.getInfo().getSerchIdParticipant().getTeamPosition().equals(line) && riotMatchDto.getInfo().getQueueId() == 420) {
                lineCount = lineCount + 1; 
            }
        }

        return lineCount;
    }



}
