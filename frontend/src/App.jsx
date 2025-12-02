import { Routes,Route, BrowserRouter} from 'react-router-dom'
import Layout from './page/components/Layout.jsx';
import Home from './page/Home.jsx';
import FindMatchInfo from './page/FindMatchInfo.jsx';
import Login from './page/Login.jsx';
import SignUp from './page/signUp.jsx';
import FindMemberBoard from './page/FindMemberBoard.jsx';
import FindMemberBoardDetail from './page/FindMemberBoardDetail.jsx';
import FindMeberBoardWrite from './page/FindMemberBoardWrite.jsx';
import FindMemberBoardUpdate from './page/FindMeberBoardUpdate.jsx';
import CommunityBoard from './page/CommunityBoard.jsx';
import CommunityBoardDetail from './page/CommunityBoardDetail.jsx';
import CommunityBoardWrite from './page/CommunityBoardWrite.jsx';
import CommunityBoardUpdate from './page/CommunityBoardUpdate.jsx';
import ErrorPage from './page/components/ErrorPage.jsx';
import TierList from './page/TierList.jsx';

function App() {

  return (

    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout></Layout>}>
            <Route index element={<Home></Home>}></Route>
            <Route path="/find" element={<FindMatchInfo></FindMatchInfo>}></Route>
            <Route path='/board/member' element={<FindMemberBoard></FindMemberBoard>}></Route>
            <Route path='/board/member/detail/:seq' element={<FindMemberBoardDetail></FindMemberBoardDetail>}></Route>
            <Route path='/board/member/wirte' element={<FindMeberBoardWrite></FindMeberBoardWrite>}></Route>
            <Route path='/board/member/update/:seq' element={<FindMemberBoardUpdate></FindMemberBoardUpdate>}></Route>
            <Route path='/board/community' element={<CommunityBoard></CommunityBoard>}></Route>
            <Route path='/board/community/detail/:seq' element={<CommunityBoardDetail></CommunityBoardDetail>}></Route>
            <Route path='/board/community/write' element={<CommunityBoardWrite></CommunityBoardWrite>}></Route>
            <Route path='/board/community/update/:seq' element={<CommunityBoardUpdate></CommunityBoardUpdate>}></Route>
            <Route path='/board/tierList' element={<TierList></TierList>}></Route>
            <Route path='/board/error' element={<ErrorPage></ErrorPage>}></Route>
        </Route>
        
            <Route path='/login' element={<Login></Login>}></Route>
            <Route path='/signUp' element={<SignUp></SignUp>}></Route>
      </Routes>
    </BrowserRouter>
    
  );
}

export default App
