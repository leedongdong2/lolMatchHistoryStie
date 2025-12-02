import { Outlet } from "react-router-dom";
import Footer from "./Footer";
import Header from "./Header";
import LoginBar from "./LoginBar";






function Layout() {
    return (
        <div>
            <div style={{backgroundColor : '#1C1C1F',height : '10vh',width:'100vw'}}>
                <Header></Header>
            </div>
            <div style={{backgroundColor : '#1C1C1F',minHeight:'80vh',width:'10vw',position:"absolute",right:'0',zIndex : '1'}}>
                <LoginBar></LoginBar>
            </div>
            <div style={{position: 'relative',backgroundColor:'#1C1C1F', minHeight : '80vh'}}>
                <main>
                    <Outlet />  {/* 자식 Route 컴포넌트가 여기 렌더됨 **/}
                </main>
            </div>
            <div style={{backgroundColor : '#1C1C1F',height:'10vh',width:'100vw'}}>
            <Footer></Footer>
            </div>
        </div>

    )
}

export default Layout;