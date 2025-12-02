import { Link } from "react-router-dom";
import {useUser} from '../reactContext/LoginUserContext.jsx'
import styles from '../../css/loginBar.module.css'
import { useNavigate } from "react-router-dom";
 

function LoginBar() {
    const token = localStorage.getItem('token');
    const {riotUser,rank,nickname} = useUser();
    const {clearUserData} = useUser();
    const navigate = useNavigate();

    const logout = ()=>{
        localStorage.removeItem('token');
        clearUserData();
        navigate('/');
    }

    return (
        <div className={styles.loginBox}>
            {nickname ? (
                    <div>
                        <div className={styles.loginNicknameBox}>{nickname}</div>
                        <div className={styles.loginUserInfoBox}>
                            <div className={styles.loginUserRankBox}>
                                <p><span className={styles.textOver}>{riotUser.gameName}#{riotUser.tagLine}</span></p>
                                <p className={styles.loginBarRankBox}><span>{rank.tier} {rank.rank} {rank.leaguePoints}점</span></p>
                                <img 
                                    src={`/riotImg/tier/${rank.tier}.png`}
                                    width={100}
                                    height={100}
                                />
                                <p className={styles.loginBarRankBox}><span>{rank.wins}승 {rank.losses}패</span></p>
                                <p className={styles.loginBarRankBox}><img src={`http://ddragon.leagueoflegends.com/cdn/15.8.1/img/profileicon/${riotUser.profileIconId}.png`} width={90} height={90}/></p>
                            </div>
                            <div className={styles.logoutBox}>
                                <button className={styles.logoutButton} onClick={logout}>로그아웃</button>
                            </div>
                        </div>
                    </div>
            ) : (
                    <div className={styles.loginFontBox}>
                        <Link to="/login">마이페이지</Link>
                    </div>
            ) }
        </div>
        ) 
        
}

export default LoginBar;