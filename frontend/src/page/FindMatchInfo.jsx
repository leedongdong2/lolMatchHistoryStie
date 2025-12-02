import { useLocation } from 'react-router-dom';
import styles from '../css/findMatchInfo.module.css';
import { useForm } from 'react-hook-form';
import React,{ useEffect,useState } from 'react';
import axios from 'axios';
import qs from 'qs';
import clsx from 'clsx';
import { useNavigate } from 'react-router-dom';
//툴팁 라이브러리 (마우스를 올리거나 클릭햇을떄 작은 정보가 나오는 박스 ui요소) 
import Tippy from '@tippyjs/react';
import 'tippy.js/dist/tippy.css'; // 스타일도 꼭 import!
import RiotTooltip from './components/RiotTooltip';
import OverViewPanal from './components/OverViewPanal';
import BuildViewPanal from './components/BuildViewPanal';
import EtcViewPanal from './components/EtcViewPanal';
import CircleLoader from 'react-spinners/CircleLoader';


function FindMatchInfo(){

const location = useLocation();
const navigate = useNavigate();
/** ----------------------전적 가져오기부분----------------------------------------------**/

const [match,setMatch] = useState({});
const [isLoading, setIsLoading] = useState(true);
const [matchType,setMatchType] =useState('');
const [matchIndex,setMatchIndex] = useState(0);
const [userData,setUserData] = useState(location.state || {});
const [searchError,setSearchError] = useState(null);

const items = [0,1,2,3,4,5,6];

const fetchMatchInfo = (matchType = '') =>{
  setIsLoading(true)
  axios.get('/riot/matchInfo',{
        params : {...userData,matchType : matchType},
        paramsSerializer : (params) => qs.stringify(params,{encode:true}) //전적검색시 유저 아이디 태그쪽에 #이 붙어서 파라미터에서 #을 그냥 문자로 인식할수잇게 qs라이브러리를
                                                                         //이용하여  파싱해주엇다 
    })
    .then(response => {
        setMatch(response.data);
        const timer = setTimeout(() => setIsLoading(false), 500);
    })
    .catch(error => {
        if(error.response.status === 400) {
            setIsLoading(false);
            setSearchError("error");
        }
    });
} 

useEffect(()=>{
    fetchMatchInfo();
},[])


const clickMatchInfo = (id,tag,region = userData.region) =>{
 const data ={riotId : `${id}#${tag}`, region : region}
 navigate('/find',{state:data});    
} 

useEffect(() => {
  if(location.state){
    setUserData(location.state);
    }
}, [location.state]);

useEffect(()=>{
    fetchMatchInfo();
},[userData])

const serchMatchbyTypeInfo = (e) => {
    const text = e.currentTarget.textContent;
    const typeMap = {
        전체 : '',
        랭크 : '420',
        칼바람 : '450'
    };
    setMatchType(typeMap[text] ?? '');
    setMatchIndex(0);
    fetchMatchInfo(typeMap[text] ?? '');
}

//더보기를 눌럿을시 매치정보를 더 불러오는 함수
const fetchMatchInfoMore = ()=>{
    const newIndex = `${matchIndex+5}`; //불러올 시점의 인덱스를 구함
    
    setIsLoading(true)  
    axios.get('/riot/matchInfo/more',{
        params : {...userData,matchType : matchType,matchStartIndex : newIndex,...match.riotWInAndLineDto},
        paramsSerializer : (params) => qs.stringify(params,{encode:true})
    })
    .then(response => {
        setMatch(prev => ({          
            ...prev,            // 이어붙이기를 할떄 ...prev <- 이전(현재 렌더링된)의 match 상태를 펼쳐짐과 동시에 다른 객체에 복사가 된다. 즉 참조값이 다른 객체가 만들어짐 (불변성)
            riotMatchDtoList : [ // match안의 riotMatchDtoList 배열을 펼쳐 이어붙여준다  ?? [] nullish 병합 연산자 를 이용하여 널값 체크 null이거나 undefined면 빈배열을 사용한다는 뜻
                ...(prev.riotMatchDtoList ?? []), // 리스트는 보통 html부분에서 map으로 펼쳐서 렌더링을 해주는데 값이 아예 널이나  undefined면 오류가 떠서  해주는게 좋다
                ...(response.data.riotMatchDtoList ?? [])
            ],
            riotMatchIdList: [ 
              ...(prev.riotMatchIdList ?? []),  
              ...(response.data.riotMatchIdList ?? [])
            ],
            riotWInAndLineDto : response.data.recentRiotWInAndLineDto ?? {} 
        }))
        setMatchIndex(newIndex);
        setIsLoading(false);
    })
    .catch(error => {
        console.log(error);
        setIsLoading(false);
    });
}

//** 전적 상세보기 토글**//
const [styleVisibleList,setStyleVisibleList] = useState([]);
const [visibleList,setVisibleList] = useState([]);

useEffect(() => {
  // 리스트 길이가 늘어났을 때, visibleList도 맞춰 늘려주기
  if (match.riotMatchDtoList && visibleList.length < match.riotMatchDtoList.length) {
    const diff = match.riotMatchDtoList.length - visibleList.length;
    setStyleVisibleList((prev) => [...prev, ...Array(diff).fill(false)]); // 동일하게 배열을 이어주는것 늘어난 배열만큼 늘리고 안을 false로 채워준다
    setVisibleList((prev)=>[...prev, ...Array(diff).fill(false)]);
  }
}, [match.riotMatchDtoList]);

//그 상태정보를 볼떄의  토글버튼
const toggleVisible = (index) => {
    //상태정보를 가져올떄 한번만 렌더 되면 굳이 계속 렌더될 필요가 없기떄문에
    //div자체의 visible과 스타일 visible을 나눈어서 토글을 해준다
    //렌더 visible을 렌더됫을떄 true로 바꾸어 값을 고정으로 넣어주고
    setVisibleList((prev)=>{
        const newList = [...prev];
        newList[index] = true;
        return newList;
    })
    //이후 stlyeVisible의 토글을 이용해서 상태정보를 껏다 켯다 할수잇게 바꿔줌
    setStyleVisibleList((prev)=>{
        const newList = [...prev];
        newList[index] = !newList[index];
        return newList;
    })
}

//이전 상태에 값을 이어 붙이거나 부분적으로 값을 바꿀떄 주의할점은
//react는 참조값만을 보고 상태의 변화를 알아채기떄문에 기존에 있던 값을 복사하여 새로운 참조값을 만들어주고
//그 새로운 참조값에 내용을 변화시켜 리턴해줘야 참조값의 변화를 알고 내용의 변화를 다시 렌더해준다.
//참조값은 동일한데 안의 내용이 바뀌엇다고 렌더를 다시 해주지않음




/** ----------------------전적 가져오기부분----------------------------------------------**/
const handleImgError = (e) => {
    e.target.onerror = null;
}








    if(isLoading) {
        return( 
                <div style={{display:'flex',justifyContent:"center",alignItems:"center",height:"60vh"}}>
                    <CircleLoader color="#123abc" loading={isLoading} size={100} />
                </div>
        )
    }

    if(searchError) { 
        return (
            <div style={{display:'flex',justifyContent:"center",gap:"10vh",flexDirection:"column", alignItems :"center",height:"60vh"}}>
                <div style={{fontWeight:"bold",fontSize:"2em",color:"#fff"}}>없는 유저입니다!!</div>
                <div style={{width:"30vw",height:"auto"}}>
                    <img src="/riotImg/onerrorImg/error.png" width="100%" height="100%"></img> 
                </div>           
            </div>
        )
    }

    return (
        <div>
            <div className={styles.findMatchSearchBox}>
                <div>
                    <form>
                        <select className={styles.findMatchSearchSelect}>
                            <option value='asia/kr'>kr</option>
                        </select>
                        <input
                            type='text'
                            className={styles.findMatchSearchInput}
                        />
                        <input
                            type='submit'
                            value='GG'
                            className={styles.findMatchSearchButton}
                        />
                    </form>
                </div>
            </div>
            <div className={styles.findMatchInfoBox}>
                <div className='flex'>
                    <div className='flex'>
                        <div className={styles.findMatchprofileBox}>
                            <div>
                                <img src={`http://ddragon.leagueoflegends.com/cdn/15.8.1/img/profileicon/${match.riotUserDto.profileIconId}.png`}
                                    width={100}
                                    height={100}
                                />
                            </div>
                            <div className={styles.findMatchSummonerLevel}>
                                {match.riotUserDto.summonerLevel}
                            </div>
                        </div>
                        <div className={styles.findMatchUserInfoBox}>
                            <div className='flex'>
                                <div className={styles.findMatchInfoUserName}>
                                    {match.riotUserDto.gameName}
                                </div> 
                                <div className={styles.findMatchInfoUserName}>
                                    #{match.riotUserDto.tagLine}
                                </div>
                            </div>
                            <div className={styles.findMatchInfoUserRankBox}>
                                <div>
                                    <img
                                        src={`/riotImg/tier/${match.rankDto.tier}.png`}
                                        width={80}
                                        height={80}
                                    />
                                </div>
                                <div className={styles.findMatchInfoUserTierBox}>
                                    <div className={styles.whiteColor}>
                                        {match.rankDto.tier} {match.rankDto.rank}
                                    </div>
                                    <div className={styles.OpacityWhiteColor} >
                                        {match.rankDto.leaguePoints} 점
                                    </div>
                                    <div className={styles.findMatchInfoUserWin}>
                                        {match.rankDto.wins}승/{match.rankDto.losses}패
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className={styles.chapionMasteryBox}>
                        {match.championMasteryDtos.map((dto,index)=>(
                            <div key={index} className={styles.chapionMasteryFontSize}>
                                <div>
                                    <img
                                        src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/champion/${dto.championKeyName}.png`}
                                        className={styles.chapionMasteryImg}
                                    />
                                </div>
                                <div className={styles.whiteColor}>
                                    {dto.championLevel}
                                </div>
                                <div className={styles.whiteColor}>
                                    {dto.championName}
                                </div>
                                <div className={styles.OpacityWhiteColor}>
                                    {dto.championPoints.toLocaleString()}
                                </div>
                                <div className={styles.OpacityWhiteColor}>
                                    pts
                                </div>
                                <div className={styles.OpacityWhiteColor}>
                                    {dto.lastPlayTimeFormat}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
                <div>
                    <div className={styles.findMatchTypeBox}>
                        <div onClick={serchMatchbyTypeInfo} 
                             className={`${styles.matchType} ${styles.findMatchTypeButton}`}
                        >
                            전체
                        </div>
                        <div onClick={serchMatchbyTypeInfo}
                             className={`${styles.matchType} ${styles.findMatchTypeButton}`}
                        >
                            랭크
                        </div>
                        <div onClick={serchMatchbyTypeInfo}
                             className={`${styles.matchType} ${styles.findMatchTypeButton}`}
                        >
                            칼바람
                        </div>
                    </div>
                    <div className={styles.recentGamesBox}>
                        <div className={styles.recentGamesFontBox}>
                            <div>Recent Games</div>
                            <div>
                                {match.riotWInAndLineDto.gameCount}전
                            </div>
                            <div>
                                {match.riotWInAndLineDto.winCount}전
                            </div>
                            <div>
                                {match.riotWInAndLineDto.loseCount}전
                            </div>
                        </div>
                        <div className={styles.linePercentBox}>
                            <div className={styles.linePercentChartBox}>
                                <div className={styles.linePercentBackgroundBar}>
                                    <div style={{
                                        width : '100%',
                                        height : `${match.riotWInAndLineDto.topLinePercent}%`,
                                        backgroundColor : '#5383E8'
                                    }}>
                                    </div>
                                </div>
                                <div className={styles.linePercentBackgroundBar}>
                                    <div style={{
                                        width : '100%',
                                        height : `${match.riotWInAndLineDto.jungleLinePercent}%`,
                                        backgroundColor : '#5383E8'
                                    }}>
                                    </div>
                                </div>
                                <div className={styles.linePercentBackgroundBar}>
                                    <div style={{
                                        width : '100%',
                                        height : `${match.riotWInAndLineDto.midLinePercent}%`,
                                        backgroundColor : '#5383E8'
                                    }}>
                                    </div>
                                </div>
                                <div className={styles.linePercentBackgroundBar}>
                                    <div style={{
                                        width : '100%',
                                        height : `${match.riotWInAndLineDto.adCarryLinePercent}%`,
                                        backgroundColor : '#5383E8'
                                    }}>
                                    </div>
                                </div>
                                <div className={styles.linePercentBackgroundBar}>
                                    <div style={{
                                        width : '100%',
                                        height : `${match.riotWInAndLineDto.supportLinePercent}%`,
                                        backgroundColor : '#5383E8'
                                    }}>
                                    </div>
                                </div>
                            </div>
                            <div className={styles.linePercentFontBox}>
                                <div className={styles.linePercentFont}>TOP</div>
                                <div className={styles.linePercentFont}>JG</div>
                                <div className={styles.linePercentFont}>MID</div>
                                <div className={styles.linePercentFont}>AD</div>
                                <div className={styles.linePercentFont}>SUP</div>
                            </div>
                        </div>
                    </div>
                {match.riotMatchDtoList.map((dto,index)=>(
                    <div key={dto.metadata.matchId} className={styles.findMatchListBox}>
                        <div className={clsx(styles.riotMatchDtoBox,styles.findMatchDtoBox,{
                                [styles.winBackground] : dto.info.serchIdWin === '승리',
                                [styles.loseBackground] : dto.info.serchIdWin === '패배'

                        })}>
                            <div className={`flex ${styles.marginTop}`}>
                                <div className={styles.searchParticipantUserBox}>
                                    <div className={styles.searchParticipantIdBox}>
                                        <div className={styles.serchIdParticipantId}></div>
                                    </div>
                                    <div className={styles.searchParticipantUserResultBox}>
                                        <div> {dto.info.queueName} </div>
                                        <div> {dto.info.gameEndTimestampPrettyTime}</div>
                                        <div className={clsx(styles.searchIdWin ,styles.searchParticipantUserWin,{
                                            [styles.winText] : dto.info.serchIdWin === '승리',
                                            [styles.loseText] : dto.info.serchIdWin === '패배'
                                        })}>
                                            {dto.info.serchIdWin}
                                        </div>
                                        <div className={styles.searchParticipantUserDurationBox}>
                                            <div>{dto.info.gameDurationMinutes}m</div>
                                            <div>{dto.info.gameDurationSeconds}s</div>
                                        </div>
                                    </div>
                                </div>
                                <div className={styles.searchParticipantUserMatchInfoBox}>
                                    <div className={styles.searchParticipantUserMatchInfo}>
                                        <span className={styles.searchParticipantChampionImg}>
                                            <img
                                                src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/champion/${dto.info.serchIdParticipant.championName}.png`}
                                                width={'100%'}
                                            /> 
                                        </span>
                                        <div>
                                            <div className={styles.searchParticipantSummonerSpellImg}>
                                                {/**롤api에서 정보를 가져올떄 태그가 달려잇어 혹시 모를 위험요소 제거와 태그의 브라우저 노출을 막기위해 dompurify 라이브러리를 사용한 RiotTooltip페이지로 값을 변환해준다  */}
                                                <Tippy content={<RiotTooltip rawHtml={`${dto.info.serchIdParticipant.summonerSpells[0].summonerSpellName} <br/><br/> ${dto.info.serchIdParticipant.summonerSpells[0].summonerDescription}`}></RiotTooltip>}>
                                                <img 
                                                    src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/spell/${dto.info.serchIdParticipant.summonerSpells[0].summonerSpellId}.png`}
                                                    width={'100%'}
                                                />
                                                </Tippy>
                                                <Tippy content={<RiotTooltip rawHtml={`${dto.info.serchIdParticipant.summonerSpells[1].summonerSpellName} <br/><br/> ${dto.info.serchIdParticipant.summonerSpells[1].summonerDescription}`}></RiotTooltip>}>
                                                <img
                                                    src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/spell/${dto.info.serchIdParticipant.summonerSpells[1].summonerSpellId}.png`}
                                                    width={'100%'}
                                                />
                                                </Tippy>
                                            </div>
                                        </div>
                                        <div>
                                            {dto.info.serchIdParticipant.rune.map((rune,index)=>(
                                                index === 0 && (
                                                        <div key={index}>
                                                            <span>
                                                                <Tippy content={<RiotTooltip rawHtml={`${rune.name} <br/><br/> ${rune.shortDesc}`}></RiotTooltip>}>
                                                                <img
                                                                    src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                                                    className={styles.searchParticipantMainRuneImg}
                                                                />
                                                                </Tippy>
                                                            </span>
                                                        </div>
                                                    )
                                                ))}
                                            <div className='flex'>
                                                {dto.info.serchIdParticipant.rune.map((rune,index)=>(
                                                    (index === 4 || index === 5) && (
                                                        <div key={index}>
                                                            <Tippy content={<RiotTooltip rawHtml={`${rune.name} <br/><br/> ${rune.shortDesc}`}></RiotTooltip>}>
                                                            <img 
                                                                src={`https://ddragon.leagueoflegends.com/cdn/img/${rune.icon}`}
                                                                className={styles.searchParticipantSubRuneImg}
                                                            />
                                                            </Tippy>
                                                        </div>
                                                    )

                                                ))}
                                            </div>
                                        </div>
                                        <div className={styles.searchParticipantScoreBox}>
                                            <div className={styles.searchParticipantKdaBox}>
                                                <div>
                                                    <span className={styles.searchParticipantKAFont}>{dto.info.serchIdParticipant.kills}/</span>
                                                    <span className={styles.searchParticipantDFont}>{dto.info.serchIdParticipant.deaths}/</span>
                                                    <span className={styles.searchParticipantKAFont}>{dto.info.serchIdParticipant.assists}</span>
                                                </div>
                                                <div className={styles.searchParticipantKDAFont}>K/D/A</div>
                                            </div>
                                            <div className={styles.searchParticipantWardScoreBox}>
                                                <div className={styles.searchParticipantWardKDFont}>
                                                    <span>{dto.info.serchIdParticipant.wardsPlaced}/{dto.info.serchIdParticipant.wardsKilled}</span>
                                                </div>
                                                <div className={styles.searchParticipantWardScore}>{dto.info.serchIdParticipant.visionScore}</div>
                                                <div className={styles.searchParticipantWardScore}>wardScore</div>
                                            </div>
                                            <div className={styles.searchParticipantCsBox}>
                                                <div className={styles.searchParticipantKAFont}>{dto.info.serchIdParticipant.neutralMinionsKilled + dto.info.serchIdParticipant.totalMinionsKilled}</div>
                                                <div className={styles.searchParticipantKDAFont}>cs</div>
                                            </div> 
                                        </div>
                                    </div>
                                    <div className={styles.searchParticipantItemBox}>
                                        {items.map((item,index)=>(
                                            <span key={index}>
                                            <Tippy content={<RiotTooltip rawHtml={`${dto.info.serchIdParticipant.items[item].name} ${dto.info.serchIdParticipant.items[item].description}`} ></RiotTooltip>}>
                                            <img 
                                                src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/item/${dto.info.serchIdParticipant['item' + item]}.png`}
                                                onError={handleImgError}
                                                className={styles.searchParticipantItemImg}
                                            />
                                            </Tippy>
                                        </span>
                                        ))}
                                    </div>
                                </div>
                            </div>
                            <div className={styles.searchMatchParticipantsBox}>
                                <div className={styles.searchMatchParticipants}>
                                    {dto.info.participants.map((searchTeam,index)=>(
                                        index < 5 && (
                                            <div key={index} className='flex'>
                                                <div>
                                                    <img
                                                        src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/champion/${searchTeam.championName}.png`}
                                                        className={styles.searchMatchParticipantsChampionImg}
                                                    />
                                                </div>
                                                <div className={`${styles.serchMatchLink} ${styles.searchMatchParticipantsName}`} onClick={()=>{clickMatchInfo(searchTeam.riotIdGameName,searchTeam.riotIdTagline)}}>
                                                    {searchTeam.riotIdGameName}
                                                </div>
                                            </div>
                                        )
                                    ))}
                                </div> 
                                <div className={styles.searchMatchParticipants}>
                                    {dto.info.participants.map((nonSearchTeam,index)=>(
                                        index > 4 && (
                                            <div key={index} className='flex'>
                                                <div>
                                                    <img 
                                                        src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/champion/${nonSearchTeam.championName}.png`}
                                                        className={styles.searchMatchParticipantsChampionImg}
                                                    />
                                                </div>
                                                    <div className={`${styles.serchMatchLink} ${styles.searchMatchParticipantsName}`} onClick={()=>{clickMatchInfo(nonSearchTeam.riotIdGameName,nonSearchTeam.riotIdTagline)}}>
                                                        {nonSearchTeam.riotIdGameName}
                                                    </div>
                                            </div>
                                        )
                                    ))}
                                </div>           
                            </div>
                            <div className={clsx(styles.showMatchInfoMore,{
                                [styles.winMoreBackground] : dto.info.serchIdWin === '승리',
                                [styles.loseMoreBackground] : dto.info.serchIdWin === '패배'
                            })}>
                                <div className={styles.overviewTogglePoint} onClick={()=>toggleVisible(index)}>
                                    More
                                </div>
                            </div>
                        </div>
                        {visibleList[index] === true && ( 
                            <div className={clsx({
                                [styles.showOverViewBox] : styleVisibleList[index] === true,
                                [styles.hideOverViewBox] : styleVisibleList[index] === false
                            })}>
                                <MatchItem matchId={match.riotMatchIdList[index]} userData={userData} participant={dto.info.serchIdParticipant.participantId}></MatchItem>
                            </div> 
                        )}
                    </div>
                    ))}
                    <div className={styles.findMatchInfoShowMoreBox} onClick={fetchMatchInfoMore}>
                        <div>
                            더보기
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}


//togglevisible 등 부모 컴포넌트에서 상태값의 변화에 따른 렌더가 많지만 자식과 관련된
//렌더는 아예 검색값이 바뀌는게 아니면 없기떄문에
//(각각의 전적마다 자식 상태정보창을 visible할떄 처음에는 부모 컴포넌트에서 상태가 바뀌기떄문에  계속 전체가 다시 렌더링 되엇다
// 이런 불필요하게 다시 렌더링 되는것을 막기위해)
//react.memo를 써 부모 컴포넌트의 렌더가 다시 되도 자식은 같이 렌더가 되지 않게 해준다
//memo는 부모가 다시 렌더링 되더라도 부모가 주는 prop의 값이 같다면 자식컴포넌트는 렌더가 되지 않게 해준다
const MatchItem = React.memo(({userData,matchId,participant}) => {
    //한번 렌더링된 상태정보 페이지를 굳이 여러번 렌더할 필요없음으로 패널객체상태를 만들어서 관리 후
    //렌더 된후에는 그냥 스타일로 visible을 결정해준다
    const [visibleSection,setVisibleSection] = useState("overViewPanal"); 
    const [mountedPanels, setMountedPanels] = useState({ 
        overViewPanal: true,
        buildPanal: false,
        etcPanal: false,
    });

    const toggleSection = (section) => {
         setVisibleSection(section); //섹션이 들어와 어느 패널을 보여줄지 고름
         setMountedPanels(prev => ({ //섹션 부분의 패널 visible을 true로 바꿈 
            ...prev,
            [section] : true
         }))
    }


    return (
        <div>
            <div className={styles.overviewButtonBox}>                              
                <div className={`${styles.matchInfoOverview} ${styles.overviewButton} ${visibleSection === 'overViewPanal' ? styles.bgVisible : ''}`} onClick={()=>toggleSection('overViewPanal')}>개요</div>
                <div className={`${styles.matchInfoBuild} ${styles.overviewButton} ${visibleSection === 'buildPanal' ? styles.bgVisible : ''}`} onClick={()=>toggleSection('buildPanal')}>빌드</div>
                <div className={`${styles.matchInfoEtc} ${styles.overviewButton} ${visibleSection === 'etcPanal' ? styles.bgVisible : ''}`} onClick={()=>toggleSection('etcPanal')}>수급량</div>
            </div>

            <div>
                {mountedPanels.overViewPanal && (
                <div style={{ display: visibleSection === "overViewPanal" ? 'block' : 'none' }}>
                    <OverViewPanal userData={userData} matchId={matchId}></OverViewPanal>
                </div>
                )}

                {mountedPanels.buildPanal && (
                <div style={{ display: visibleSection === "buildPanal" ? 'block' : 'none' }}>
                    <BuildViewPanal userData={userData} matchId={matchId} participant={participant}></BuildViewPanal>
                </div>
                )}

                {mountedPanels.etcPanal && (
                <div style={{ display: visibleSection === "etcPanal" ? 'block' : 'none' }}>
                    <EtcViewPanal userData={userData} matchId={matchId} participant={participant}></EtcViewPanal>
                </div>
                )}
            </div>
        </div>

        
    );
});









export default FindMatchInfo;