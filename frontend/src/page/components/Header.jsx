import { Link } from "react-router-dom";

/**헤더 */
function Header() {
    return (
            <div style={{paddingLeft: '1vw',height: '6vh',display: "flex",gap : '0.7vw',alignItems : "center"}}>
                <div>
                    <div style={{color: '#fff',fontSize : '1.2vw' ,fontWeight: 900}}><Link to="/">DD.GG</Link></div> 
                </div>
                <div>
                    <div style={{color: '#565666',fontWeight : 600}}><Link to="/board/community">자유게시판</Link></div>
                </div>
                <div>   
                    <div style={{color : '#565666',fontWeight : 600}}><Link to="/board/member">듀오매칭</Link></div> 
                </div> 
                <div>
                    <div style={{color : '#565666',fontWeight : 600}}><Link to="/board/tierList">티어표만들기</Link></div> 
                </div> 
            </div>
    )
}

export default Header;