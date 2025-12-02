import axios from "axios";
import { useState,useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate,useSearchParams } from "react-router-dom";
import { Link } from "react-router-dom";
import styles from "../css/findMemberBoard.module.css";

function FindMemberBoard() {
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
    const [data,setData] = useState({});
    const [isLoading,setIsLoading] = useState(true);
    
    //쿼리스트링으로 들어온 파라미터 값을 쓸수 잇게 만들어준다.
    const [searchParams, setSearchParams] = useSearchParams();


        //기본값을 설정해준다
        const page = Number(searchParams.get("page")) || 1;
            const searchType = searchParams.get("searchType") || "";
            const searchText = searchParams.get("searchText") || "";
            const matchType = searchParams.get("matchType") || "";

            //기본값을 넣어준다
            const { register, handleSubmit, setValue } = useForm({
                defaultValues: {
                searchType: searchType,
                searchText: searchText,
                matchType: matchType
                }
            });

    const getBoard = async()=>{
       try {
        const response = await axios.get('/board/findMember',{
            params : {
                page,
                searchType,
                searchText,
                matchType                
            }
        });
        const data = response.data;
        setData(data);
        setIsLoading(false);
       } catch (error) {
        navigate("/board/error");    
       } 
    }

useEffect(()=>{
   getBoard();
},[]) 

  useEffect(() => {
    getBoard();
  }, [page, searchType, searchText, matchType]);

  const onSubmit = (data) => {
    setSearchParams({
      page: "1", // 검색 시 무조건 1페이지부터
      ...data,
    });
  };


  //파라미터가 변경될떄마다 바뀐 값을 세팅해줌
  //검색한 텍스트를 남겨준다  (검색텍스트 파라미터로 값이 감 파라미터에 값을 이용 setValue를 해줌) 
    useEffect(() => {
    setValue("searchType", searchType);
    setValue("searchText", searchText);
    setValue("matchType", matchType);
  }, [searchType, searchText, matchType, setValue]);

    const getFindMemberDetail = (seq) => {
        navigate(`/board/member/detail/${seq}`)
    }

    const moveFindmemberWritePgae =  ()=>{
        if(token === null) {
            alert("로그인 후 이용해주세요");
            return;
        } 
        navigate("/board/member/wirte")
    }
    const list = data?.findMemberBoardList ?? [];
    const recentList = data?.recentTenFindMemberBoard ?? [];
    
    if(isLoading) {
        return (
            <div>로딩중 입니다..</div>
        )
    }

    return (
        <>        
        <div className={styles.commonMainBox}>
            <div className={styles.recentTenBox}>
                <div className={styles.recentTenTextBox}>
                    Latest 10 list
                </div>
                <div>
                {recentList.map((list,index)=>(
                    <Link to={`/board/member/detail/${list.seq}`}>
                        <div className={`${styles.recentTenFindMemberBoardList} ${styles.recentTenBoardBox}`} key={list.seq}>
                            <div className={styles.recentTenBoardView}>{list.views}</div>
                            <div>
                                <div className={styles.recentTenBoardContentBox}>
                                    <div className={styles.recentTenBoardTitle}>{list.title}</div>
                                    <div className={styles.recentTenBoardCommentCount}>[{list.commentCount}]</div>
                                </div>
                                <div className={styles.recentTenBoardAuthor}>{list.nickname}</div>
                            </div>
                        </div>
                    </Link>
                ))}  
                </div>
            </div>
            <div className={styles.mainBoardBox}>
                <div className={styles.boardSearchBox}>
                    <div className="flex">
                        <div className={styles.boardTitle}>
                            듀오매칭
                        </div>
                            <div className={styles.boardWriteText} onClick={()=>{moveFindmemberWritePgae()}}>
                                    글쓰기
                            </div>
                    </div>
                    <div className={styles.findMemberBoardSearchOptionBox}>
                        <form onSubmit={handleSubmit(onSubmit)}>
                            <select className={styles.findMemberBoardMatchType} 
                            {...register('matchType')}>
                                <option value="">전체</option>
                                <option value="랭크">랭크</option>
                                <option value="칼바람">칼바람</option>
                                <option value="사용자 지정">사용자 지정</option>
                                <option value="우르프">우르프</option>
                            </select>
                            <select className={styles.findMemberBoardSearchType} 
                            {...register('searchType')}>
                                <option value="">전체</option>
                                <option value="title">제목</option>
                                <option value="nickname">작성자</option>
                            </select>
                            <input type="text" className={styles.findMemberBoardSearchText} {...register('searchText')}></input>
                            <button type="submit" className={styles.findMemberBoardSearchButton}>검색</button>
                        </form>
                    </div>
                </div>
                {list === null && (
                    <div>
                        내용이 없습니다..
                    </div>
                )}
                {list !== null && (
                    list.map((list,index)=>(
                        <div className={`${styles.findMemberBoardBox} ${styles.boardContentBox}`} onClick={()=>{getFindMemberDetail(`${list.seq}`)}} key={list.seq}>
                            <div className={styles.boardViewCount}>
                                <span className={styles.e0e4d6FontColor}>{list.views}</span>
                            </div>
                            <div className={styles.boardContent}>
                                <div className={styles.minusMarginBottom}>
                                    <strong className={styles.e0e4d6FontColor}>{list.title}</strong>
                                    <span className={styles.commentCount}>[{list.commentCount}]</span>
                                </div>
                                <div className={styles.boardContentInfo}>
                                    <span>{list.prettyTime} | </span>
                                    <span>{list.matchType} | </span>
                                    <span>{list.nickname}</span>
                                </div>
                            </div>
                        </div>
                    ))
                )}
                <div className={styles.boardPageBox}>
                    <button onClick={() => setSearchParams({
                    page: "1",
                    searchType,
                    searchText,
                    matchType
                    })} className={styles.boardPageb}>
                    &laquo; 처음
                    </button>
                    {/**시작 페이지부터 끝 페이지까지의 버튼갯수를 구한후 배열을 만들어준다*/}
                    {Array.from({ length: data.page.endPage - data.page.startPage + 1 }, (_, idx) => {
                    const i = data.page.startPage + idx; {/**현재 페이지의 버튼 6,7,8,9,10 */}
                    return (
                        <button
                        key={i}
                        onClick={() => setSearchParams({//서치파람의 상태를 바꾸어 useEffect를 이용  getBoard()를 사용한다.
                            page: i.toString(), //현재페이지
                            searchType,
                            searchText,
                            matchType
                        })}
                        className={`${i === data.page.page ? styles.active : ''} ${styles.boardPage}`}
                        >
                        {i}
                        </button>
                    );
                    })}

                    <button onClick={() => setSearchParams({
                    page: data.page.totalPages.toString(),
                    searchType,
                    searchText,
                    matchType
                    })} className={styles.boardPageb}>
                    마지막 &raquo;
                    </button>
                </div> 
            </div>
        </div>
    </>
    )
}

export default FindMemberBoard;