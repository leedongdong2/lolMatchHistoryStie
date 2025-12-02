package com.reactpf.reactpf.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.reactpf.reactpf.dto.ChampionDto;
import com.reactpf.reactpf.dto.ChampionMasteryDto;
import com.reactpf.reactpf.dto.CustomUserPrincipal;
import com.reactpf.reactpf.dto.GoldAndCsDto;
import com.reactpf.reactpf.dto.ItemsDto;
import com.reactpf.reactpf.dto.RankDto;
import com.reactpf.reactpf.dto.RiotMatchDto;
import com.reactpf.reactpf.dto.RiotMatchTimeLineDto;
import com.reactpf.reactpf.dto.RiotUserDto;
import com.reactpf.reactpf.dto.RiotWInAndLineDto;
import com.reactpf.reactpf.dto.RuneTreeDto;
import com.reactpf.reactpf.dto.SummonerSpellDto;
import com.reactpf.reactpf.dto.UserDto;
import com.reactpf.reactpf.service.RiotMatchInfoService;
import com.reactpf.reactpf.service.RiotWebApiService;
import com.reactpf.reactpf.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/riot")
@RequiredArgsConstructor//생성자를 자동으로 만들어줌(롬복)
public class RiotController {

    private final RiotWebApiService riotWebApiService;
    private final RiotMatchInfoService riotMatchInfoService;
    private final UserService userService;



    @GetMapping("/rotation")
    public ResponseEntity<Map<String,Object>> getRotationChampion() {
         Map<String,Object> dataMap = new HashMap<>();
        List<String> rotationChampionsIds = riotWebApiService.getRotationsChampionIds();
        List<String> rotationChampionsNames = new ArrayList<>();

        ChampionDto championDto = riotWebApiService.getChampionDto();

        for(String championId : rotationChampionsIds) {
                rotationChampionsNames.add(riotMatchInfoService.getChampionKeyName(championDto, championId));
        }
        
        dataMap.put("rotationChampionsNames", rotationChampionsNames);

        return ResponseEntity.ok(dataMap);
    }


     /**
     * @param session
     * @param authentication
     * 
     * 유저가 로그인시 해당유저의 id의 롤id,접속지역 정보를 통해 
     * 유저의 롤id,랭크 티어 정보,프로필이미지를 가져온다. 
    */
    @GetMapping("/login/user")
    public ResponseEntity<Map<String,Object>> getLoginUserInfo(Authentication authentication) {
        String userId = authentication.getName();
        UserDto user = userService.getUserInfo(userId);
        String[] regionSplit = user.getRegion().split("/");

        RiotUserDto riotUserDto = new RiotUserDto();

        //해당 유저의 puuid를 가져온다
        riotUserDto = riotWebApiService.getPuuId(user.getLolName(), user.getLolNametag(), regionSplit[0]);
        //해당 유저의 롤id에 대한 정보를 가져온다
        riotUserDto = riotWebApiService.getRiotUserDto(riotUserDto.getPuuid(), regionSplit[1],riotUserDto.getGameName(),riotUserDto.getTagLine());
        System.out.println(riotUserDto);
        RankDto rankDto = new RankDto();

        //랭크 정보를 가져온다
        rankDto = riotWebApiService.getRiotUserRankDto(riotUserDto.getPuuid(), regionSplit[1]);

            Object principal = authentication.getPrincipal();
            String nickname = "";
          if (principal instanceof CustomUserPrincipal customUser) {
              nickname =   customUser.getNickname();// ✅ 가능
          }
          System.out.println(nickname);
        
        Map<String,Object> data = new HashMap<>();
        
        data.put("riotUserDto", riotUserDto);
        data.put("rankDto", rankDto);
        data.put("nickname",nickname);
        data.put("userId",userId);

        return ResponseEntity.ok(data);
    }







      /**
   * 
   * @param riotId
   * @param region
   * @param matchType
   * @param matchStartIndex
   * 
   * 전적검색시 전적목록을 보여준다
   * 검색id의 상세정보(kda,아이템,룬,승패 등)을 보여주고
   * 그외 id들은 챔피언 이미지와 id만 보여준다
   */
    @GetMapping("/matchInfo")
    public ResponseEntity<Map<String,Object>> getRiotMatchInfo(@RequestParam("riotId")String riotId,@RequestParam("region")String region,@RequestParam(required = false,value = "matchType",defaultValue = ("")) String matchType,RedirectAttributes redirectAttributes) {
    
    String[] riotIdSplit = riotId.split("#");
    String[] regionSplit = region.split("/");
    riotId = riotIdSplit[0];
    String riotTag = riotIdSplit[1];
    String matchStartIndex = "0";
    region = regionSplit[0];
    String lang = regionSplit[1];

    //puuId를 받아옴
    RiotUserDto riotUserDto =  riotWebApiService.getPuuId(riotId, riotTag, region);

    Map<String,Object> map = new HashMap<>();
    ItemsDto itemsDto = riotWebApiService.getItemDto();

    String riotPuuid = riotUserDto.getPuuid();
    riotId = riotUserDto.getGameName();
    riotTag = riotUserDto.getTagLine();

    
    //받아온 puuId를 통해 세부 유저의 정보를 다시 받아온다 프로필id, 소환사 레벨등
    riotUserDto = riotWebApiService.getRiotUserDto(riotPuuid, lang,riotId,riotTag);

    if(riotUserDto == null) {
        map.put("messege", "없는 아이디입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }    

    //소환사의 랭크 정보도 받아옴
    RankDto rankDto = riotWebApiService.getRiotUserRankDto(riotPuuid, lang);

        //datadragon에서 제공하는 스펠 정보를 받아온다
    SummonerSpellDto summonerSpellDto = riotWebApiService.getSummonerSpellDto();
    //datadragon에서 제공하는 룬 정보를 받아온다
    List<RuneTreeDto> runeTreeDto = riotWebApiService.getRuneTreeDto(); 
    
    
    //----숙련도 받아오기-----------------------------------------------------------------//

    //소환사의 숙련도 챔피언정보를 받아옴 (상위 4개)
    List<ChampionMasteryDto> championMasteryDtos = riotWebApiService.getChampionMasteryDto(riotUserDto.getPuuid(), lang);
    //롤api 숙련도에는 챔피언id만 있고 이름이 없기떄문엥 롤 데이터 드래곤을 통해 일단 모든 챔피언의 정보를 받아온다  
    ChampionDto championDto = riotWebApiService.getChampionDto();

    //받아온 챔피언 정보에서 챔피언 id가 각자 맞는 챔피언의 이름을 받아줌 
    riotMatchInfoService.getChamponMasteryNames(championMasteryDtos, championDto);


     //-----------전적정보 검색---------------------------------------------------------------------//  

    //puuid로 해당 소환사의 매치아이디리스트를 받아옴
    List<String> riotMatchIdList = riotWebApiService.getRiotMatchId(riotUserDto.getPuuid(), region, matchType, matchStartIndex);
    //받아온 매치아이디를 이용해서 전적정보를 받아온다 (매치아이디리스트의 갯수만큼 반복문을 돌려 모든 아이디의 전적정보를 받아와 리스트에 담아줌)


    //매치아이디리스트를 통해 매칭된 게임정보들을 가져온다
    final List<RiotMatchDto> riotMatchDtoList = riotMatchInfoService.getRiotMatchDtos(riotMatchIdList, region, riotTag);

    //검색한 id의 유저에 대한 게임 내 정보를 가져온다
    riotMatchInfoService.getSearchIdParticipant(riotMatchDtoList, riotId, riotTag);
    //게임 종류(랭크,칼바람 등)의 이름을 가져온다
    riotMatchInfoService.getRiotMatchDtoQueueName(riotMatchDtoList);
    //datadragon와 상호작용이 안되는 챔피언 이름을 알맞게 바꿔준다 
    riotMatchInfoService.changeRiotChampionName(riotMatchDtoList);
    //검색한 id 유저의 승패를 알아온다
    riotMatchInfoService.setSearchIdTeamAndWin(riotMatchDtoList);
    //검색한 id 유저의 룬 정보를 알아온다
    riotMatchInfoService.setSearchIdRuneInfo(riotMatchDtoList, runeTreeDto);
    //검색한 id 유저의 아이템 정보를 알아온다
    riotMatchInfoService.setSearchIdItemInfo(riotMatchDtoList, itemsDto);
    //검색한 id 유저의 스펠 정보를 알아온다
    riotMatchInfoService.setSearchIdSpellInfo(riotMatchDtoList, summonerSpellDto);
    //게임 지속 시간 밑 종료시간을 프리티타임으로 가져옴
    riotMatchInfoService.setGameDurationAndendDate(riotMatchDtoList);


    final RiotWInAndLineDto riotWInAndLineDto = new RiotWInAndLineDto();

    riotWInAndLineDto.setGameCount(riotMatchDtoList.size());
    riotWInAndLineDto.setWinCount(riotMatchInfoService.setWinAndLoseCount("승리", riotMatchDtoList));
    riotWInAndLineDto.setLoseCount(riotMatchInfoService.setWinAndLoseCount("패배", riotMatchDtoList));
    
    riotWInAndLineDto.setTopLineCount(riotMatchInfoService.calculateLineCount("TOP", riotMatchDtoList));
    riotWInAndLineDto.setMidLineCount(riotMatchInfoService.calculateLineCount("MIDDLE", riotMatchDtoList));
    riotWInAndLineDto.setJungleLineCount(riotMatchInfoService.calculateLineCount("JUNGLE", riotMatchDtoList));
    riotWInAndLineDto.setAdCarryLineCount(riotMatchInfoService.calculateLineCount("BOTTOM", riotMatchDtoList));
    riotWInAndLineDto.setSupportLineCount(riotMatchInfoService.calculateLineCount("UTILITY", riotMatchDtoList)); 
    
    riotWInAndLineDto.setTopLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getTopLineCount(), riotWInAndLineDto.getGameCount()));
    riotWInAndLineDto.setMidLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getMidLineCount(), riotWInAndLineDto.getGameCount()));
    riotWInAndLineDto.setJungleLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getJungleLineCount(), riotWInAndLineDto.getGameCount()));
    riotWInAndLineDto.setAdCarryLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getAdCarryLineCount(), riotWInAndLineDto.getGameCount()));
    riotWInAndLineDto.setSupportLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getSupportLineCount(), riotWInAndLineDto.getGameCount()));    

    
    
    
    
    
    
    
    
            
        
        map.put("riotWInAndLineDto", riotWInAndLineDto);
        map.put("riotMatchIdList", riotMatchIdList);
        map.put("riotMatchDtoList", riotMatchDtoList);
        map.put("riotUserDto", riotUserDto);
        map.put("rankDto", rankDto);
        map.put("championMasteryDtos",championMasteryDtos);
        map.put("riotId", riotId + "#" + riotTag);
        map.put("region", region +"/"+ lang);
        map.put("matchType", matchType);
        return ResponseEntity.ok(map);
    }


       /**
   * 
   * @param riotId
   * @param region
   * @param matchType
   * @param matchStartIndex
   * 
   * 전적검색시 전적목록을 보여준다
   * 검색id의 상세정보(kda,아이템,룬,승패 등)을 보여주고
   * 그외 id들은 챔피언 이미지와 id만 보여준다
   */
  @GetMapping("/matchInfo/more")
  public ResponseEntity<Map<String,Object>> riotMactchInfoShowMore(@RequestParam("riotId")String riotId,@RequestParam("region")String region,@RequestParam(required = false,value = "matchType") String matchType,@RequestParam(required =  false,value="matchStartIndex",defaultValue = "0") String matchStartIndex,RiotWInAndLineDto riotWInAndLineDto) {
    System.out.println(riotWInAndLineDto.getGameCount());

    String[] riotIdSplit = riotId.split("#");
    String[] regionSplit = region.split("/");
    riotId = riotIdSplit[0];
    String riotTag = riotIdSplit[1];
    region = regionSplit[0];
    String lang = regionSplit[1];
    

        //puuId를 받아옴
    RiotUserDto riotUserDto =  riotWebApiService.getPuuId(riotId, riotTag, region);
    
    //datadragon에서 제공하는 아이템 정보를 받아온다
    ItemsDto itemsDto = riotWebApiService.getItemDto(); 

    String riotPuuid = riotUserDto.getPuuid();
    riotId = riotUserDto.getGameName();
    riotTag = riotUserDto.getTagLine();

    //받아온 puuId를 통해 세부 유저의 정보를 다시 받아온다 프로필id, 소환사 레벨등
    riotUserDto = riotWebApiService.getRiotUserDto(riotPuuid, lang,riotId,riotTag);
    
    //datadragon에서 제공하는 스펠 정보를 받아온다
    SummonerSpellDto summonerSpellDto = riotWebApiService.getSummonerSpellDto();
    //datadragon에서 제공하는 룬 정보를 받아온다
    List<RuneTreeDto> runeTreeDto = riotWebApiService.getRuneTreeDto(); 



    //-----------전적정보 검색---------------------------------------------------------------------//  

    //puuid로 해당 소환사의 매치아이디리스트를 받아옴
    List<String> riotMatchIdList = riotWebApiService.getRiotMatchId(riotUserDto.getPuuid(), region, matchType, matchStartIndex);
    //받아온 매치아이디를 이용해서 전적정보를 받아온다 (매치아이디리스트의 갯수만큼 반복문을 돌려 모든 아이디의 전적정보를 받아와 리스트에 담아줌)


    //매치아이디리스트를 통해 매칭된 게임정보들을 가져온다
    final List<RiotMatchDto> riotMatchDtoList = riotMatchInfoService.getRiotMatchDtos(riotMatchIdList, region, riotTag);

    //검색한 id의 유저에 대한 게임 내 정보를 가져온다
    riotMatchInfoService.getSearchIdParticipant(riotMatchDtoList, riotId, riotTag);
    //게임 종류(랭크,칼바람 등)의 이름을 가져온다
    riotMatchInfoService.getRiotMatchDtoQueueName(riotMatchDtoList);
    //datadragon와 상호작용이 안되는 챔피언 이름을 알맞게 바꿔준다 
    riotMatchInfoService.changeRiotChampionName(riotMatchDtoList);
    //검색한 id 유저의 승패를 알아온다
    riotMatchInfoService.setSearchIdTeamAndWin(riotMatchDtoList);
    //검색한 id 유저의 룬 정보를 알아온다
    riotMatchInfoService.setSearchIdRuneInfo(riotMatchDtoList, runeTreeDto);
    //검색한 id 유저의 아이템 정보를 알아온다
    riotMatchInfoService.setSearchIdItemInfo(riotMatchDtoList, itemsDto);
    //검색한 id 유저의 스펠 정보를 알아온다
    riotMatchInfoService.setSearchIdSpellInfo(riotMatchDtoList, summonerSpellDto);
    //게임 지속 시간 밑 종료시간을 프리티타임으로 가져옴
    riotMatchInfoService.setGameDurationAndendDate(riotMatchDtoList);


    
    riotWInAndLineDto.setGameCount(riotMatchDtoList.size() + riotWInAndLineDto.getGameCount());
    riotWInAndLineDto.setWinCount(riotMatchInfoService.setWinAndLoseCount("승리", riotMatchDtoList) + riotWInAndLineDto.getWinCount());
    riotWInAndLineDto.setLoseCount(riotMatchInfoService.setWinAndLoseCount("패배", riotMatchDtoList) + riotWInAndLineDto.getLoseCount());



    riotWInAndLineDto.setTopLineCount(riotMatchInfoService.calculateLineCount("TOP", riotMatchDtoList) + riotWInAndLineDto.getTopLineCount());
    riotWInAndLineDto.setMidLineCount(riotMatchInfoService.calculateLineCount("MIDDLE", riotMatchDtoList) + riotWInAndLineDto.getMidLineCount() );
    riotWInAndLineDto.setJungleLineCount(riotMatchInfoService.calculateLineCount("JUNGLE", riotMatchDtoList) + riotWInAndLineDto.getJungleLineCount());
    riotWInAndLineDto.setAdCarryLineCount(riotMatchInfoService.calculateLineCount("BOTTOM", riotMatchDtoList) + riotWInAndLineDto.getAdCarryLineCount());
    riotWInAndLineDto.setSupportLineCount(riotMatchInfoService.calculateLineCount("UTILITY", riotMatchDtoList) + riotWInAndLineDto.getSupportLineCount()); 
    
    riotWInAndLineDto.setTopLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getTopLineCount(), riotWInAndLineDto.getGameCount()));
    riotWInAndLineDto.setMidLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getMidLineCount(), riotWInAndLineDto.getGameCount()));
    riotWInAndLineDto.setJungleLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getJungleLineCount(), riotWInAndLineDto.getGameCount()));
    riotWInAndLineDto.setAdCarryLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getAdCarryLineCount(), riotWInAndLineDto.getGameCount()));
    riotWInAndLineDto.setSupportLinePercent(riotMatchInfoService.calculateLineCountPercent(riotWInAndLineDto.getSupportLineCount(), riotWInAndLineDto.getGameCount())); 


    Map<String,Object> dataMap = new HashMap<>();

    dataMap.put("recentRiotWInAndLineDto", riotWInAndLineDto);
    dataMap.put("riotMatchIdList", riotMatchIdList);
    dataMap.put("riotMatchDtoList", riotMatchDtoList);

    return ResponseEntity.ok(dataMap);
    }


    /**
     * 
     * 
     * @param riotId
     * @param region
     * @param riotMatchId
     * @return
     * 
     * 전적검색시의 전적목록중 전적의 상세내용을 보여준다
     * 검색한 id의 게임정보 뿐만 아니라 모든 유저의 게임 정보를 보여준다
     * 팀별로 오브젝트와 킬 얻은 골드량 등을 보여준다  
     */
    @GetMapping("/matchInfo/overView")
    public ResponseEntity<Map<String,Object>> test(@RequestParam("riotId")String riotId,@RequestParam("region")String region,@RequestParam("riotMatchId") String riotMatchId) {

        String[] riotIdSplit = riotId.split("#");
        String[] regionSplit = region.split("/");
        riotId = riotIdSplit[0];
        String riotTag = riotIdSplit[1];
        region = regionSplit[0];
        int serchedIdTeamId = 0; 
        //datadragon에서 룬 정보를 받아온다
        List<RuneTreeDto> runeTreeDto = riotWebApiService.getRuneTreeDto(); 
        //datadragon에서 스펠정보를 받아온다
        SummonerSpellDto summonerSpellDto = riotWebApiService.getSummonerSpellDto();
        //datadragon에서 아이템 정보를 받아온다
        ItemsDto itemsDto = riotWebApiService.getItemDto();

        //게임매치아이디를 통해 플레이한 게임정보를 알아온다.
        final RiotMatchDto riotMatchDto = riotWebApiService.getRiotMatchInfo(riotMatchId, region);
        //datadragon와 상호작용이 안되는 챔피언 이름을 알맞게 바꿔준다 
        riotMatchInfoService.changeRiotChampionName(riotMatchDto);
        //검색된 아이디의 게임 내 정보를 알아온다.
        riotMatchInfoService.getSearchIdParticipant(riotMatchDto, riotId, riotTag);
        //검색된 아이디의 게임 내 승패를 알아온다
        riotMatchInfoService.setSearchIdTeamAndWin(riotMatchDto);
        //검색된 아이디의 게임 내 팀아이디(블루.레드)을 알아온다
        serchedIdTeamId = riotMatchDto.getInfo().getSerchIdParticipant().getTeamId();
        //게임 내 아이디들의 룬 정보를 알아온다
        riotMatchInfoService.setMatchParticipantsRuneInfo(riotMatchDto, runeTreeDto);
        //게임 내 아이디들의 아이템 정보를 알아온다
        riotMatchInfoService.setMatchParticipantsItemInfo(riotMatchDto, itemsDto);
        //게임 내 아이디들의 스펠 정보를 알아온다
        riotMatchInfoService.setMatchParticipantsSpellInfo(riotMatchDto, summonerSpellDto);
        //게임 내 아이디들을 검색한 아이디 유저의 팀과 반대 팀으로 분류해준다
        riotMatchInfoService.groupByTeamSearchId(riotMatchDto, serchedIdTeamId);
        //팀별 전체 얻은 골드량을  계산한다
        riotMatchInfoService.calculateTotalGoldPerTeam(riotMatchDto);
        //팀별로 게임 내 유저들의 kda비율을 구해준다
        riotMatchInfoService.calculateKdaForParticipants(riotMatchDto.getInfo().getSerchIdTeamParticipants());
        riotMatchInfoService.calculateKdaForParticipants(riotMatchDto.getInfo().getNonSerchIdTeamParticipants());
        //팀별로 게임 내 유저들의 팀 전체 킬에 대한 관여율을 퍼센트로 구해준다
        riotMatchInfoService.calculateKPForParticipants(riotMatchDto.getInfo().getSerchIdTeamParticipants());
        riotMatchInfoService.calculateKPForParticipants(riotMatchDto.getInfo().getNonSerchIdTeamParticipants());
        //팀별로 게임 내 유저들의 챔피언에 가한 피해량을 퍼센트로 구해준다
        riotMatchInfoService.calculateMaxDamagePercentForParticipants(riotMatchDto.getInfo().getSerchIdTeamParticipants());
        riotMatchInfoService.calculateMaxDamagePercentForParticipants(riotMatchDto.getInfo().getNonSerchIdTeamParticipants());
        //팀별로 게임 내 유저들의 받은 피해량을 퍼센트로 구해준다
        riotMatchInfoService.calculateMaxTakenPercentForParticipants(riotMatchDto.getInfo().getSerchIdTeamParticipants());
        riotMatchInfoService.calculateMaxTakenPercentForParticipants(riotMatchDto.getInfo().getNonSerchIdTeamParticipants()); 
        //팀별로 게임 내 유저들의 분당 cs수급량을 구해준다
        riotMatchInfoService.calculateCSPMForParticipants(riotMatchDto.getInfo().getSerchIdTeamParticipants(),riotMatchDto.getInfo().getGameDuration());
        riotMatchInfoService.calculateCSPMForParticipants(riotMatchDto.getInfo().getNonSerchIdTeamParticipants(),riotMatchDto.getInfo().getGameDuration()); 
        //팀별 게임전체 킬수의 팀당 킬수의 퍼센트  구해준다
        riotMatchInfoService.calculateKpPerTeam(riotMatchDto);
        //팀별 게임전체 얻은 골드량의 팀당 얻은 골드량 퍼센트를 구해준다
        riotMatchInfoService.calculateTotalGoldPercentPerTeam(riotMatchDto);

        //블루 팀 레드팀 정보따로 뿌리기위해  따로 담아준다
        RiotMatchDto.TeamDto buleTeamDto = riotMatchDto.getInfo().getTeams().get(0);
        RiotMatchDto.TeamDto redTeamDto = riotMatchDto.getInfo().getTeams().get(1);
        
        Map<String,Object> dataMap = new HashMap<>();
        
        dataMap.put("buleTeamDto", buleTeamDto);
        dataMap.put("redTeamDto", redTeamDto);
        dataMap.put("riotMatchDto", riotMatchDto);
        return ResponseEntity.ok(dataMap);
    }


    /**
     * 
     * @param riotId
     * @param region
     * @param riotMatchId
     * @param participantId
     * 검색한 유저의 전적정보 중 게임내 아이템 구입순서
     * 스킬트리, 채용룬의 정보를 알려준다 
     */
    @GetMapping("/matchInfo/build")
    public ResponseEntity<Map<String,Object>> getMatchInfoBuild(@RequestParam("riotId")String riotId,@RequestParam("region")String region,@RequestParam("riotMatchId") String riotMatchId,@RequestParam("participantId") int participantId) {

    String[] riotIdSplit = riotId.split("#");
    String[] regionSplit = region.split("/");
    riotId = riotIdSplit[0];
    String riotTag = riotIdSplit[1];
    region = regionSplit[0];
    //datadragon에서 룬 정보를 얻어온다
    List<RuneTreeDto> runeTreeDto = riotWebApiService.getRuneTreeDto();
    //datadragon에서 아이템 정보를 얻어온다 
    ItemsDto itemsDto = riotWebApiService.getItemDto(); 
    //게임 내 타임라인 정보를 알아온다(분마다 일어나는 이벤트(스킬을 찍엇다던가,아이템을 구매햇다던가 등)) 
    RiotMatchTimeLineDto riotMatchTimeLineDto = riotWebApiService.getRiotMatchTimeLineDto(riotMatchId, region);

        //타임라인을 이용하여 4분주기마다 검색한 유저의 아이템 구매 내역을 알아온다//
    final Map<Integer, List<RiotMatchTimeLineDto.EventDTO>> eventDtos = riotMatchInfoService.getItemPurchasesEvery4Minutes(riotMatchTimeLineDto, participantId);
    
    //구매한 아이템들의 정보를 알아온다
    riotMatchInfoService.setItemInfo(eventDtos, itemsDto);

    //타임라인중 검색유저의 스킬레벨을 올렷을떄의 이벤트들만 모아서 찾아온다
    List<RiotMatchTimeLineDto.EventDTO> skillLevelUpEvent = riotMatchInfoService.getSkillLevelUp(riotMatchTimeLineDto, participantId); 
    
    //게임 정보를 알아온다
    RiotMatchDto riotMatchDto = riotWebApiService.getRiotMatchInfo(riotMatchId, region);

    //검색한 유저의 게임 내 정보를 얻어온다
    riotMatchInfoService.getSearchIdParticipant(riotMatchDto, riotId, riotTag);
    //검색한 유저의 룬 정보를 알아온다
    riotMatchInfoService.setSearchIdRuneInfo(riotMatchDto, runeTreeDto);

    //메인룬과 서브룬의 정보들을 알아와서 따로 담아준다
    List<RiotMatchDto.Rune> mainRunes = new ArrayList<>();
    List<RiotMatchDto.Rune> subRunes = new ArrayList<>();  
    //메인룬은 0~3까지 서브룬은 4~5까지이다
    for(int i=0;i<4;i++){
        mainRunes.add(riotMatchDto.getInfo().getSerchIdParticipant().getRune().get(i));
    }

    for(int i=4;i<6;i++){
        subRunes.add(riotMatchDto.getInfo().getSerchIdParticipant().getRune().get(i));
    }


    Map<String,Object> data = new HashMap<>();

    data.put("skillLevelUp", skillLevelUpEvent);
    data.put("mainRunes", mainRunes);
    data.put("subRunes", subRunes);
    data.put("eventDtos", eventDtos);
    return ResponseEntity.ok(data);
    }

    @GetMapping("/matchInfo/etc")
    public ResponseEntity<Map<String,List<Integer>>> getMatchInfoEtcChart(@RequestParam("riotId")String riotId,@RequestParam("region")String region,@RequestParam("riotMatchId") String riotMatchId,@RequestParam("participantId") int participantId) {
        
        String[] riotIdSplit = riotId.split("#");
        String[] regionSplit = region.split("/");
        riotId = riotIdSplit[0];
        region = regionSplit[0];


    //매치 아이디를 이용하여 게임내 타임라인을 얻어온다
    RiotMatchTimeLineDto riotMatchTimeLineDto = riotWebApiService.getRiotMatchTimeLineDto(riotMatchId, region);
        //4분주기로 검색된 유저의 골드와 cs수급량을 알아온다
    Map<Integer, GoldAndCsDto> goldAndCs = riotMatchInfoService.getGoldAndCsEvery4Minutes(riotMatchTimeLineDto, participantId);

            
    //챠트로 쓸수 잇게 데이터를 가공해준다
    Map<String,List<Integer>> goldAndCsChartData = riotMatchInfoService.getGoldAndCsChartData(goldAndCs);


        return ResponseEntity.ok(goldAndCsChartData);
    }


    
    @GetMapping("/champion")
    public ResponseEntity<List<ChampionDto.Champion>> geChampionDto(@RequestParam(name = "tag",required = false) String tag) { 
        ChampionDto championDto = riotWebApiService.getChampionDto();
        List<ChampionDto.Champion> champions = riotMatchInfoService.getTagChampion(championDto, tag);
        return ResponseEntity.ok(champions);
    }
}
