import axios from "axios"
import React,{ useEffect, useState } from "react"
import styles from '../../css/findMatchInfo.module.css';
import Tippy from '@tippyjs/react';
import 'tippy.js/dist/tippy.css'; // 스타일도 꼭 import!
import { useNavigate } from 'react-router-dom';
import RiotTooltip from "./RiotTooltip";
import qs from "qs";

function OverViewPanal({userData,matchId}) { 
    const[data,setData] = useState({});
    const[isLoading,setIsLoading] = useState(true);
    const navigate = useNavigate();
    const items = [0,1,2,3,4,5,6];

    useEffect(()=>{
     axios('riot/matchInfo/overView',{
        params : {...userData,
            riotMatchId : matchId
        },
        paramsSerializer : (params) => qs.stringify(params,{encode:true})
     })
     .then(response => {
        setData(response.data);
        setIsLoading(false);
      })
      .catch(error => {
        console.log(error);
        setIsLoading(false);
      })    
    },[userData.riotId, userData.region, matchId])


    

    const info = data?.riotMatchDto?.info;
    const blueTeam = data?.buleTeamDto;
    const redTeam = data?.redTeamDto;

    if(isLoading) {
        return (
            <div>
                로딩중입니다.....
            </div>
        )
    }

    return (
        <div>
            <div className={styles.teamBox}>
                <div className={styles.teamStateBox}>
                    <div className={styles.teamState}>
                        <div>{info.serchIdTeam}</div>
                        <div>{info.serchIdWin}</div>
                    </div>
                    <div style={{width : '9%'}}>KDA</div>
                    <div style={{width : '11%'}}>Damage</div>
                    <div style={{width : '8%'}}>wards</div>
                    <div style={{width : '18%'}}>CS</div>
                    <div>Items</div>
                </div>
                <div className={styles.searchIdTeamBox}>
                {info.serchIdTeamParticipants.map((participant,index)=>(
                    <div key={participant.summonerId} className={styles.teamParticipantInfoBox}>
                        <div className={styles.teamParticipantBuildBox}>
                            <div>
                                <div className={styles.teamParticipantNameLevelBox}>
                                    <img src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/champion/${participant.championName}.png`}
                                         className={styles.teamParticipantChampionImg}
                                    />
                                    <div className={styles.teamParticipantChampionLevel}>{participant.champLevel}</div>
                                </div>
                            </div>
                            <div>
                                <Tippy content={<RiotTooltip rawHtml={`${participant.summonerSpells[0].summonerSpellName} <br/><br/> ${participant.summonerSpells[0].summonerDescription}`}></RiotTooltip>}>
                                <div><img src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/spell/${participant.summonerSpells[0].summonerSpellId}.png`}
                                          className={styles.teamParticipantSummonerSpellImg}
                                     />
                                </div>
                                </Tippy>
                                <Tippy content={<RiotTooltip rawHtml={`${participant.summonerSpells[1].summonerSpellName} <br/><br/> ${participant.summonerSpells[1].summonerDescription}`}></RiotTooltip>}>
                                <div><img src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/spell/${participant.summonerSpells[1].summonerSpellId}.png`}
                                          className={styles.teamParticipantSummonerSpellImg}
                                     />
                                </div>
                                </Tippy>
                            </div>
                            <div>
                            {participant.rune.map((rune,index)=>(
                                index === 0 && (
                                    <div key={rune.key}>
                                    <Tippy content={<RiotTooltip rawHtml={`${rune.name} <br/><br/> ${rune.shortDesc}`}></RiotTooltip>}>
                                        <img src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                             className={styles.teamParticipantMainRuneImg}
                                        />
                                    </Tippy>
                                    </div>
                                )
                            ))}
                                <div  className='flex'>
                                {participant.rune.map((rune,index)=>(
                                    (index === 4 || index === 5) && (
                                        <div key={rune.key}>
                                        <Tippy content={<RiotTooltip rawHtml={`${rune.name} <br/><br/> ${rune.shortDesc}`}></RiotTooltip>}>
                                            <img src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                                className={styles.teamParticipantSubRuneImg}
                                            />
                                        </Tippy>
                                        </div>
                                    )
                                ))}
                                </div>    
                            </div>
                        </div>
                        <div className={styles.teamParticipantScoreBox}>
                            <div className={`${styles.serchMatchLink} ${styles.teamParticipantId}`}>{participant.riotIdGameName}</div>
                            <div className={styles.teamParticipantKdaBox}>
                                <div>{participant.kills}/{participant.deaths}/{participant.assists} ({participant.kdaPercent}%)</div>
                                <div>{participant.kda}</div>
                            </div>
                            <div className={styles.teamParticipantDamegePercentBox}>
                                <div>{participant.totalDamageDealtToChampions.toLocaleString()}</div>
                                <div className={styles.teamParticipantDamegePercentBackgroundBar}>
                                    <div style={{
                                            width : `${participant.totalDamageDealtToChampionsPercent}%`,
                                            backgroundColor : '#E84057',
                                            height : '100%'
                                    }}>
                                    </div>
                                </div>
                            </div>
                            <div className={styles.teamParticipantDamegeTakenPercentBox}>
                                <div>{participant.totalDamageTaken.toLocaleString()}</div>
                                <div className={styles.teamParticipantDamegeTakenPercentBackgroundBar}>
                                    <div style={{
                                        width : `${participant.totalDamageTakenPercent}%`,
                                        backgroundColor : '#7B7A8E',
                                        height : '100%'
                                    }}>
                                    </div>
                                </div>
                            </div>
                            <div className={styles.teamParticipantWardScoreBox}>
                                <div>{participant.visionScore}</div>
                                <div>
                                    <div>{participant.wardsPlaced}/{participant.wardsKilled}</div>
                                </div>
                            </div>
                            <div className={styles.teamParticipantCsBox}>
                                <div>{participant.neutralMinionsKilled + participant.totalMinionsKilled}</div>
                                <div>{participant.cspm}</div>
                            </div>
                        </div>
                        <div className={styles.teamParticipantItemBox}>
                            {items.map((item,index)=>(
                            <Tippy content={<RiotTooltip rawHtml={`${participant.items[item].name} ${participant.items[item].description}`}></RiotTooltip>}>
                            <div key={item.name}><img src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/item/${participant['item' + index]}.png`} className={styles.teamParticipantItemImg}/></div>
                            </Tippy>
                            ))}
                        </div>
                    </div>    
                ))}
                </div>
            </div>

            <div className={styles.totalTeamObjectScoreBox}>
                <div className={styles.teamObjectScoreBox}>
                    <div className={styles.blueTeamFont}>블루팀</div>
                    <div className={styles.teamObjectScoreSize}>
                        <div>
                            <span className={styles.blueTeamObjectScoreName}>바론</span>
                            <span className={styles.teamObjectScore}>{blueTeam.objectives.baron.kills}</span>
                            <span className={styles.blueTeamObjectScoreName}>아타칸</span>
                            <span className={styles.teamObjectScore}>{blueTeam.objectives.atakhan.kills}</span>
                            <span className={styles.blueTeamObjectScoreName}>드래곤</span>
                            <span className={styles.teamObjectScore}>{blueTeam.objectives.atakhan.kills}</span>
                            <span className={styles.blueTeamObjectScoreName}>유충</span>
                            <span className={styles.teamObjectScore}>{blueTeam.objectives.atakhan.kills}</span>
                        </div>
                        <div>
                            <span className={styles.blueTeamObjectScoreName}>전령</span>
                            <span className={styles.teamObjectScore}>{blueTeam.objectives.riftHerald.kills}</span>
                            <span className={styles.blueTeamObjectScoreName}>억제기</span>
                            <span className={styles.teamObjectScore}>{blueTeam.objectives.inhibitor.kills}</span>
                            <span className={styles.blueTeamObjectScoreName}>타워</span>
                            <span className={styles.teamObjectScore}>{blueTeam.objectives.tower.kills}</span>
                        </div>
                    </div>
                </div>
                <div style={{width : '40%'}}>
                    <div className={styles.teamTotalKillBox}>
                        <div style={{width : `${blueTeam.teamKillPercent}%`,
                                     backgroundColor : '#5383E8',
                                     height : '100%'
                        }}>
                            {blueTeam.objectives.champion.kills}
                        </div>
                        <div style={{width : `${redTeam.teamKillPercent}%`,
                                     backgroundColor : '#E84057',
                                     height : '100%',
                                     textAlign : 'right'

                        }}>
                            {redTeam.objectives.champion.kills}
                        </div>
                        <div className={styles.teamKillGoldFontPosition}>Total Kill</div>
                    </div>
                    <div className={styles.teamTotalGoldBox}>
                        <div style={{width : `${blueTeam.teamTotalGoldPercent}%`,
                                     backgroundColor : '#5383E8',
                                     height : '100%'
                        }}>
                            {blueTeam.teamTotalGold}
                        </div>
                        <div style={{width : `${redTeam.teamTotalGoldPercent}%`,
                                     backgroundColor : '#E84057',
                                     height : '100%',
                                     textAlign : 'right'

                        }}>
                            {redTeam.teamTotalGold}
                        </div>
                        <div className={styles.teamKillGoldFontPosition}>Total Gold</div>
                    </div>
                </div>
                <div className={styles.teamObjectScoreBox}>
                    <div className={styles.redTeamObjectScoreSize}>
                        <div>
                            <span className={styles.redTeamObjectScoreName}>바론</span>
                            <span className={styles.teamObjectScore}>{redTeam.objectives.baron.kills}</span>
                            <span className={styles.redTeamObjectScoreName}>아타칸</span>
                            <span className={styles.teamObjectScore}>{redTeam.objectives.atakhan.kills}</span>
                            <span className={styles.redTeamObjectScoreName}>드래곤</span>
                            <span className={styles.teamObjectScore}>{redTeam.objectives.atakhan.kills}</span>
                            <span className={styles.redTeamObjectScoreName}>유충</span>
                            <span className={styles.teamObjectScore}>{redTeam.objectives.atakhan.kills}</span>
                        </div>
                        <div>
                            <span className={styles.redTeamObjectScoreName}>전령</span>
                            <span className={styles.teamObjectScore}>{redTeam.objectives.riftHerald.kills}</span>
                            <span className={styles.redTeamObjectScoreName}>억제기</span>
                            <span className={styles.teamObjectScore}>{redTeam.objectives.inhibitor.kills}</span>
                            <span className={styles.redTeamObjectScoreName}>타워</span>
                            <span className={styles.teamObjectScore}>{redTeam.objectives.tower.kills}</span>
                        </div>
                    </div>
                    <div className={styles.redTeamFont}>레드팀</div>
                </div>
            </div>

            <div className={styles.teamBox}>
                <div className={styles.teamStateBox}>
                    <div className={styles.teamState}>
                        <div>{info.nonSerchIdTeam}</div>
                        <div>{info.nonSerchIdWin}</div>
                    </div>
                    <div style={{width : '9%'}}>KDA</div>
                    <div style={{width : '11%'}}>Damage</div>
                    <div style={{width : '8%'}}>wards</div>
                    <div style={{width : '18%'}}>CS</div>
                    <div>Items</div>
                </div>
                <div className={styles.nonSearchIdTeamBox}>
                {info.nonSerchIdTeamParticipants.map((participant,index)=>(
                    <div key={participant.summonerId} className={styles.teamParticipantInfoBox}>
                        <div className={styles.teamParticipantBuildBox}>
                            <div>
                                <div className={styles.teamParticipantNameLevelBox}>
                                    <img src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/champion/${participant.championName}.png`}
                                         className={styles.teamParticipantChampionImg}
                                    />
                                    <div className={styles.teamParticipantChampionLevel}>{participant.champLevel}</div>
                                </div>
                            </div>
                            <div>
                                <div>
                                    <Tippy content={<RiotTooltip rawHtml={`${participant.summonerSpells[0].summonerSpellName} <br/><br/> ${participant.summonerSpells[0].summonerDescription}`}></RiotTooltip>}>
                                    <img src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/spell/${participant.summonerSpells[0].summonerSpellId}.png`}
                                          className={styles.teamParticipantSummonerSpellImg}
                                     />
                                     </Tippy>
                                </div>
                                <div>
                                    <Tippy content={<RiotTooltip rawHtml={`${participant.summonerSpells[1].summonerSpellName} <br/><br/> ${participant.summonerSpells[1].summonerDescription}`}></RiotTooltip>}>
                                    <img src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/spell/${participant.summonerSpells[1].summonerSpellId}.png`}
                                          className={styles.teamParticipantSummonerSpellImg}
                                     />
                                     </Tippy>
                                </div>
                            </div>
                            <div>
                            {participant.rune.map((rune,index)=>(
                                index === 0 && (
                                    <div key={rune.key}>
                                    <Tippy content={<RiotTooltip rawHtml={`${rune.name} <br/><br/> ${rune.shortDesc}`}></RiotTooltip>}>
                                        <img src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                             className={styles.teamParticipantMainRuneImg}
                                        />
                                    </Tippy>
                                    </div>
                                )
                            ))}
                                <div  className='flex'>
                                {participant.rune.map((rune,index)=>(
                                    (index === 4 || index === 5) && (
                                        <div key={rune.key}>
                                        <Tippy content={<RiotTooltip rawHtml={`${rune.name} <br/><br/> ${rune.shortDesc}`}></RiotTooltip>}>
                                            <img src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                                className={styles.teamParticipantSubRuneImg}
                                            />
                                        </Tippy>
                                        </div>
                                    )
                                ))}
                                </div>    
                            </div>
                        </div>
                        <div className={styles.teamParticipantScoreBox}>
                            <div className={`${styles.serchMatchLink} ${styles.teamParticipantId}`}>
                                {participant.riotIdGameName}
                            </div>
                            <div className={styles.teamParticipantKdaBox}>
                                <div>{participant.kills}/{participant.deaths}/{participant.assists} ({participant.kdaPercent}%)</div>
                                <div>{participant.kda}</div>
                            </div>
                            <div className={styles.teamParticipantDamegePercentBox}>
                                <div>{participant.totalDamageDealtToChampions.toLocaleString()}</div>
                                <div className={styles.teamParticipantDamegePercentBackgroundBar}>
                                    <div style={{
                                            width : `${participant.totalDamageDealtToChampionsPercent}%`,
                                            backgroundColor : '#E84057',
                                            height : '100%'
                                    }}>
                                    </div>
                                </div>
                            </div>
                            <div className={styles.teamParticipantDamegeTakenPercentBox}>
                                <div>{participant.totalDamageTaken.toLocaleString()}</div>
                                <div className={styles.teamParticipantDamegeTakenPercentBackgroundBar}>
                                    <div style={{
                                        width : `${participant.totalDamageTakenPercent}%`,
                                        backgroundColor : '#7B7A8E',
                                        height : '100%'
                                    }}>
                                    </div>
                                </div>
                            </div>
                            <div className={styles.teamParticipantWardScoreBox}>
                                <div>{participant.visionScore}</div>
                                <div>
                                    <div>{participant.wardsPlaced}/{participant.wardsKilled}</div>
                                </div>
                            </div>
                            <div className={styles.teamParticipantCsBox}>
                                <div>{participant.neutralMinionsKilled + participant.totalMinionsKilled}</div>
                                <div>{participant.cspm}</div>
                            </div>
                        </div>
                        <div className={styles.teamParticipantItemBox}>
                            {items.map((item,index)=>(
                            <Tippy content={<RiotTooltip rawHtml={`${participant.items[item].name} ${participant.items[item].description}`}></RiotTooltip>}>
                                <div key={item.name}><img src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/item/${participant['item' + index]}.png`} className={styles.teamParticipantItemImg}/></div>
                            </Tippy>
                            ))}
                        </div>
                    </div>    
                ))}
                </div>
            </div>            
        </div>
    )
}

export default React.memo(OverViewPanal);