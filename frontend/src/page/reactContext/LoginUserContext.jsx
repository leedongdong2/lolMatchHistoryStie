import { createContext,useState,useContext} from 'react';

//react에서 공유할 데이터를 만들어 주는 요소
//리액트 컴포넌트들과의 전역변수를 만들어주는 훅이다
const LoginUserContext = createContext();

export const UserProvider = ({children}) => {
    const [riotUser,setRiotUser] = useState(null);
    const [rank,setRank] = useState(null);
    const [nickname,setNickname] = useState(null);
    const [userId,setUserId] = useState(null);
 
    
    const setUserData = ({riotUserDto,rankDto,nickname,userId}) => {
        setRiotUser(riotUserDto);
        setRank(rankDto);
        setNickname(nickname);
        setUserId(userId)
    }

    const clearUserData = () => {
        setRiotUser(null);
        setRank(null);
        setNickname(null);
        setUserId(null);
    }

    return (
        //칠드런 부분을 덮으면 칠드런 부분에서 전역변수로 사용 가능하다
        <LoginUserContext.Provider value={{riotUser,rank,nickname,userId,setUserData,clearUserData}}>
            {children}
        </LoginUserContext.Provider>
    );
};

//useUser()로 객체들을 불러와 사용해줌 (예 const {userId} = useUser();)
export const useUser = () => useContext(LoginUserContext);

