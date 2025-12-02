import React,{useEffect,useState} from "react"
import qs from 'qs';
import axios from "axios";
import styles from '../../css/findMatchInfo.module.css';
import Tippy from '@tippyjs/react';
import 'tippy.js/dist/tippy.css'; // 스타일도 꼭 import!!
import RiotTooltip from "./RiotTooltip";

function BuildViewPanal({userData,matchId,participant}) {
    const[data,setData] = useState({});
    const[isLoading,setIsLoading] = useState(true);

    useEffect(()=>{
        axios.get('/riot/matchInfo/build',{
            params : {...userData,
                riotMatchId : matchId,
                participantId : participant
            },
            paramsSerializer : (params) => qs.stringify(params,{encode:true})
        })
        .then(response=>{
            setData(response.data);
            setIsLoading(false)
        })
        .catch(error=>{
            console.log(error)
            setIsLoading(false)
        })

    },[]);
    const eventDtos = data?.eventDtos ? Object.entries(data.eventDtos) : [];
    const skillDto = data?.skillLevelUp; 
    const mainRune = data?.mainRunes;
    const subRune = data?.subRunes;

    if(isLoading === true) {
        return(
            <div>
                로딩중입니다....
            </div>
        )
    }

    return (
        <div>
            <div className={styles.itemBuildBox}>
                <div className={styles.itemBuildTitleBox}>
                    <div className={styles.itemBuildTitle}>
                        Item Build
                    </div>
                </div>
                <div className={styles.itemBuildTimeLineBox}>
                    {eventDtos.map(([key,entry],index)=>(
                        <div key={key}>
                            <div className={styles.itemBuildTimeFont}>
                                {(parseInt(key)+1)*4}분
                            </div>
                            <div className={styles.itemBuildTimeLineItemBox}>
                                {index !== 0 && (
                                    <div className={styles.itemBuildTimeLineArrow}>
                                        &gt;
                                    </div>
                                )}
                                {Object.entries(entry).map(([key,value],index)=>(
                                        <div key={key}>
                                        <Tippy content={<RiotTooltip rawHtml={`${value.itemName} <br/> ${value.itemDescription}`}></RiotTooltip>}>
                                            <img
                                                src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/item/${value.itemId}.png`}
                                                className={styles.itemBuildTimeLineItemImg}
                                            />
                                        </Tippy>
                                        </div>
                                ))}
                            </div>
                        </div>    
                    ))}
                </div>
            </div>
            <div className={styles.skillBuildTimeLineBox}>
                <div className={styles.skillBuildTitleBox}>
                    <div className={styles.skillBulidTitle}>
                        Skill
                    </div>
                </div>
                <div className={styles.skillTreeBox}>
                    {skillDto.map((skill,index)=>(
                        <div key={index}>
                            {skill.skillKey === 'Q' && (
                               <div className={styles.Qskill}>
                                {skill.skillKey}
                               </div>
                            )}
                            {skill.skillKey === 'W' && (
                               <div className={styles.Wskill}>
                                {skill.skillKey}
                               </div>
                            )}
                            {skill.skillKey === 'E' && (
                               <div className={styles.Eskill}>
                                {skill.skillKey}
                               </div>
                            )}
                            {skill.skillKey === 'R' && (
                               <div className={styles.Rskill}>
                                {skill.skillKey}
                               </div>
                            )}                            
                        </div>
                    ))}
                </div>
            </div>
            <div className={styles.runeBuildBox}>
                <div className={styles.runeBuildTitle}>
                    Rune
                </div>
                <div className={styles.runeTreeBox}>
                    <div className="flex">
                        {mainRune.map((rune,index)=>(
                            <div key={index} className={styles.mainRuneBox}>
                                {index === 0 && (
                                    <Tippy content={<RiotTooltip rawHtml={`${rune.name}<br/>${rune.shortDesc}`}></RiotTooltip>}>
                                    <img
                                        src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                        className={styles.mainAttributeRuneImg}
                                    />
                                    </Tippy>
                                )}
                            </div>    
                        ))}
                        {mainRune.map((rune,index)=>(
                            <div key={index} className={styles.mainRuneBox}>
                                {index !== 0 && (
                                    <Tippy content={<RiotTooltip rawHtml={`${rune.name}<br/>${rune.shortDesc}`}></RiotTooltip>}>
                                    <img
                                        src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                        className={styles.subAttributeRuneImg}
                                    />
                                    </Tippy>
                                )}
                            </div>    
                        ))}
                    </div>
                    <div className={styles.subRuneBox}>
                        {subRune.map((rune,index)=>(
                            <div key={index}>
                                <Tippy content={<RiotTooltip rawHtml={`${rune.name}<br/>${rune.shortDesc}`}></RiotTooltip>}>
                                <img
                                    src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                    className={styles.subRuneImg} 
                                />
                                </Tippy>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )

}

export default React.memo(BuildViewPanal);