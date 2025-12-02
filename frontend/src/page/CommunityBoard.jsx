
import { useSearchParams } from "react-router-dom";
import { useEffect,useState } from "react";
import { useForm } from "react-hook-form";
import axios from "axios";
import styles from '../css/community.module.css';
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
function CommunityBoard() {
    const [isLoading,setIsLoading] = useState(true);
    const [boardData,SetBoardData] = useState({});  
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    const [searchParams, setSearchParams] = useSearchParams();

    const page = Number(searchParams.get("page")) || 1;
    const searchType = searchParams.get("searchType") || "all";
    const searchText = searchParams.get("searchText") || "";
    const searchOrder = searchParams.get("searchOrder") || "Latest";
    
    
    
    const getBoard = async()=> {
        const response = await axios.get('/board/community',{
            params : {
                page,
                searchType,
                searchText,
                searchOrder
            }
        })
        SetBoardData(response.data);

    }

    useEffect(()=>{
        const data = async()=> {
            try {
                await getBoard();
                setIsLoading(false);
            } catch (error) {
                navigate("/board/error")    
            }
        }

        data();
    },[]);
        

    const {
        register,
        handleSubmit,
        setValue
    } = useForm();

      //파라미터가 변경될떄마다 바뀐 값을 세팅해줌
    //검색한 텍스트를 남겨준다 (검색텍스트 파라미터로 값이 감 파라미터에 값을 이용 setValue를 해줌) 
        useEffect(() => {
        setValue("searchType", searchType);
        setValue("searchText", searchText);
        setValue("searchOrder", searchOrder);
    }, [searchType, searchText, searchOrder, setValue]);
    
      useEffect(() => {
        setIsLoading(true);
        const data = async()=> {
            try {
                await getBoard();
                setIsLoading(false);
            } catch (error) {
                navigate("/board/error")       
            }
        }

        data();
      }, [page, searchType, searchText, searchOrder]);



    const onSubmit = async (data)=> {
        setSearchParams({
            page : "1",
            searchOrder : "Latest",
            ...data
        })
    }

    const ChangeSearchOrder = (order)=> {
        setSearchParams(prev => ({
            ...prev,
            searchOrder : order
        })) 
    }

    const getDetail = (seq)=>{
        navigate(`/board/community/detail/${seq}`);
    }

    const getWrite = ()=> {
        if(token === null) {
            alert("로그인 후 이용해주세요");
            return;
        }

        navigate("/board/community/write");
    }

    const list = boardData?.communityListDto ?? [];
    const recentList = boardData?.recentTenCommunity ?? [];
    
    if(isLoading) {
        return (
            <div>
                로딩중입니다
            </div>
        )
    }
    
        return(
            <div className={styles.commonMainBox}>
                <div className={styles.recentTenBox}>
                    <div className={styles.recentTenTextBox}>
                        Latest 10 list
                    </div>
                    <div>
                        {recentList.map((list,index)=>(
                            <Link to={`/board/community/detail/${list.seq}`}>
                                <div key={list.seq} className={styles.recentTenBoardBox}>
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
                    <div>
                        <div className={styles.boardSearchBox}>
                            <div className="flex">
                                <p className={styles.boardTitle}>자유게시판</p>
                                <div className={styles.boardWriteText} onClick={()=>{getWrite()}}>글쓰기</div>
                            </div>
                            <div className={styles.communitySearchBox}>
                                <div className={styles.communitySearchViewOption}>
                                    <span className={styles.searchOrder} onClick={()=>{ChangeSearchOrder("Latest")}}>Latest</span>
                                    <span className={styles.searchOrder} onClick={()=>{ChangeSearchOrder("Popular")}}>  Popular</span>
                                </div>
                                <div className={styles.communitySearchOptionBox}>
                                    <form onSubmit={handleSubmit(onSubmit)}>
                                        <select
                                          className={styles.communitySearchType}
                                          {...register('searchType')}
                                        >
                                            <option value="all">전체</option>
                                            <option value="title">제목</option>
                                            <option value="author">작성자</option>
                                            <option value="content">내용</option>
                                        </select>
                                        <input
                                            type="text"
                                            className={styles.communitySearchText}
                                            {...register('searchText')}
                                        />
                                        <button type="submit" className={styles.communitySearchButton}>검색</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                        {Array.isArray(list) && list.length > 0 ? (
                            <div>
                                <div className="marginTop">
                                    {list.map((list,index)=>(
                                        <div className={styles.boardContentBox} onClick={()=>{getDetail(list.seq)}}>
                                            <div className={styles.boardViewCount}>
                                                <span className={styles.e0e4d6FontColor}>{list.views}</span>
                                            </div>
                                            <div className={styles.boardContent}>
                                                <div className={styles.minusMarginBottom}>
                                                    <strong className={styles.e0e4d6FontColor}>{list.title}</strong>
                                                    <span className={styles.commentCount}>[{list.commentCount}]</span>
                                                </div>
                                                <div className={styles.boardContentInfo}>
                                                    <span>{list.prettyTime}</span>
                                                    <span>  | {list.nickname}</span>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>

                                <div className={styles.boardPageBox}>
                                    <button onClick={() => setSearchParams({
                                        page: "1",
                                        searchType,
                                        searchText,
                                        searchOrder
                                        })} className={styles.boardPageb}>
                                        &laquo; 처음
                                        </button>

                                        {Array.from({ length: boardData.page.endPage - boardData.page.startPage + 1 }, (_, idx) => {
                                        const i = boardData.page.startPage + idx;
                                        return (
                                            <button
                                            key={i}
                                            onClick={() => setSearchParams({
                                                page: i.toString(),
                                                searchType,
                                                searchText,
                                                searchOrder
                                            })}
                                            className={`${i === boardData.page.page ? styles.active : ''} ${styles.boardPage}`}
                                            >
                                            {i}
                                            </button>
                                        );
                                        })}

                                        <button onClick={() => setSearchParams({
                                        page: boardData.page.totalPages.toString(),
                                        searchType,
                                        searchText,
                                        searchOrder
                                        })} className={styles.boardPageb}>
                                        마지막 &raquo;
                                        </button>
                                </div>
                            </div>
                        ) : (
                            <div className="marginTop">
                                내용이 없습니다..
                            </div>
                        )}



                    </div>
                </div>
            </div>    
        )    
}

export default CommunityBoard;