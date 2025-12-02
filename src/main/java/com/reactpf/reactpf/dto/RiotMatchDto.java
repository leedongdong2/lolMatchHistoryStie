package com.reactpf.reactpf.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiotMatchDto {
    
    private Metadata metadata;
    private Info info;

    @Getter
    @Setter
    public static class Metadata {
        private String dataVersion;
        private String matchId;
        private List<String> participants;
    }

    @Getter
    @Setter
    public static class Info {
        private long gameId;
        private List<Participant> participants;
        private List<Participant> serchIdTeamParticipants = new ArrayList<Participant>();
        private List<Participant> nonSerchIdTeamParticipants = new ArrayList<Participant>();
        private Participant serchIdParticipant = new Participant();
        private long gameDuration;	//게임지속시간
        private long gameEndTimestamp; //게임종료시간
        private long gameDurationMinutes;
        private long gameDurationSeconds;
        private String gameEndTimestampPrettyTime;
        private int  queueId; //큐타입	
        private String platformId; //서버 id
        private String queueName;
        private String serchIdTeam;
        private String serchIdWin;
        private int serchIdWinCount;
        private String nonSerchIdTeam;
        private String nonSerchIdWin;
        private List<TeamDto> teams;

    }

    @Getter
    @Setter
    public static class TeamDto {
        private int teamId;
        private List<BanDto> bans;
        private ObjectivesDto objectives;
        private boolean win;
        private int teamTotalGold;
        private int teamTotalGoldPercent;
        private int teamKillPercent;
    }

    @Getter
    @Setter
    public static class BanDto {
        private int championId;
    }

    @Getter
    @Setter
    public static class ObjectivesDto {
        private ObjectiveDto baron; //바론
        private ObjectiveDto atakhan; // 아타칸
        private ObjectiveDto champion; // 챔피언
        private ObjectiveDto dragon; //드래곤
        private ObjectiveDto horde; //유충
        private ObjectiveDto inhibitor; //억제기
        private ObjectiveDto riftHerald; // 전령
        private ObjectiveDto tower; //타워
    }

    @Getter
    @Setter
    public static class ObjectiveDto {
        private boolean first; //퍼스트블러드
        private int kills; //킬수
    }

    @Getter
    @Setter
    public static class Participant {
        private String summonerId;  //소환사 아이디
        private int profileIcon; //프로필 아이콘
        private int summonerLevel; //소환사 레벨
        private String riotIdGameName;//소환사 닉네임
        private String riotIdTagline;//태그
        private int participantId; //게임 내 소환사 순번

        private Challenges challenges;
        
        private boolean win; //승리
        private int teamId; //팀아이디
        
        private int championId; //챔피언 아이디
        private String championName; //챔피언 이름
        private int champLevel; //챔피언 레벨 
        private int summoner1Id; //스펠1 아이디
        private int summoner2Id;//스펠2 아이디
        private List<SummonerSpell> summonerSpells = new ArrayList<>();


        private int kills; //킬
        private int deaths; //데스
        private int assists;  //어시
        private double kda;    
        private int kdaPercent;

        private int largestMultiKill; //순간 최다킬(더블킬/트리플/쿼드라/펜타)	
        
        private int item0; //아이템
        private int item1;
        private int item2;
        private int item3;
        private int item4;
        private int item5;
        private int item6;
        
        private int goldEarned; // 벌어드린골드
        private int goldSpent; // 사용골드
        
        private int totalDamageDealtToChampions; //챔피언에게 넣은 총 데미지
        private int physicalDamageDealtToChampions; // 챔피언에게 넣은 물리 데미지
        private int magicDamageDealtToChampions; //챔피언에게 넣은 마법 데미지
        private int trueDamageDealtToChampions; //챔피언에게 넣은 고정 데미지
        private int damageDealtToBuildings;//건물에게 넣은 데미지
        private int damageDealtToTurrets;//포탑에 넣은 데미지
        private int totalDamageDealtToChampionsPercent;



        private int totalDamageTaken; //받은딜
        private int physicalDamageTaken; //받은 물리피해량
        private int magicDamageTaken;//받은 마법피해량
        private int trueDamageTaken;//받은 고정피해량
        private int damageSelfMitigated;//감소한 피해량
        private int totalDamageTakenPercent;


        
        private int neutralMinionsKilled; //cs
        private int totalMinionsKilled;
        private double cspm;


        private int visionScore; //와드점수
        private int wardsPlaced;  //와드사용
        private int wardsKilled;  //설치제거
        private int voidMonsterKill;

        private int totalDamageShieldedOnTeammates; // 아군에게 준 쉴드 총합
        private int totalHealsOnTeammates;//아군에게 준 회복량 총합


        private int totalTimeSpentDead;//사망시간 합
        private String teamPosition;//팀 포지션

        private int doubleKills; //더블킬
        private int tripleKills; //트리플킬
        private int quadraKills; //쿼드라킬
        private int pentaKills; //펜타킬

        private int turretKills; // 타워 부순 갯수
        private int inhibitorKills; // 억제기 부순 갯수
        private int dragonKills;//드래곤 킬 갯수
        private int baronKills;//바론 킬 갯수
        private int riftHeraldKills;//전령 킬 갯수

        private boolean firstBloodKill;//퍼블여부
        private boolean firstTowerKill;//퍼블포탑여부
        private boolean gameEndedInSurrender;//서렌여부
        private boolean gameEndedInEarlySurrender;//조기서렌여부
        
        private PerksDto perks;
        private Item item;
        private List<Rune> rune = new ArrayList<Rune>();
        private List<Item> items = new ArrayList<Item>();

        public List<Integer> getItemList(){
             List<Integer> items = new ArrayList<>();
             items = Arrays.asList(item0,item1,item2,item3,item4,item5,item6);
             return items;
        }

    }

    @Getter
    @Setter
    public static class PerksDto {
        private List<PerkStyleDto> styles; //룬
    }

    @Getter
    @Setter
    public static class PerkStyleDto {
        private String description; // 주룬 ,부룬  
        private int style;  // 아마 그 머냐 지배 결의 이런거
        private List<PerkStyleSelectionDto> selections; // 룬 설정 감전 돌발일격 어쩌구
    }

    @Getter
    @Setter    
    public static class PerkStyleSelectionDto {
        private int perk;
        private int var1;
        private int var2;
        private int var3;
    }

    @Getter
    @Setter
    public static class Challenges {
        private float damageTakenOnTeamPercentage; // 내가 받은 피해량 퍼센트(팀전체중)
        private int soloKills;//솔로킬
        private float teamDamagePercentage; //내가 준 피해량 퍼센트(팀전체중)
    }

    @Getter
    @Setter
    public static class Rune {
        private int id;
        private String key;
        private String icon;
        private String name;
        private String shortDesc;
        private String longDesc;
        private String tooltip;
    }

    @Getter
    @Setter
    public static class Item { 
        private String name;
        private String description;
    }
    
    @Getter
    @Setter
    public static class SummonerSpell { 
            String summonerSpellId;
            String summonerSpellName;
            String summonerDescription; 
    }


}
