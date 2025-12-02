import axios from "axios";
import { useEffect,useState,useRef } from "react";
import { useParams } from "react-router-dom";
import styles from "../css/communityDetail.module.css"
import { useUser } from "./reactContext/LoginUserContext";
import { Viewer } from '@toast-ui/react-editor';
import '@toast-ui/editor/dist/toastui-editor.css';
import '@toast-ui/editor/dist/theme/toastui-editor-dark.css'; 
import CommunityDetailComment from "./components/CommunityDetailComment.jsx"
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

function CommunityBoardDetail() {

    const [detailData,setDetailData] = useState({});
    const [detailCommentData,setDetailCommentData] = useState({});
    const [isLoading,setIsLoading] = useState(true);
    const [currentPage,setCurrentPage] = useState(1);
    const [commentText,setCommentText] = useState("");

    const navigate = useNavigate();
    const {seq} = useParams();
    const {nickname} = useUser();
    const editorRef = useRef();
    const token = localStorage.getItem("token");

    const getDetail = async() => {
        const response = await axios.get("/board/community/detail",{
            params : {
                communitySeq : seq
            }
        })
        setDetailData(response.data);
        console.log(response.data.communityDetailDto.content);
    }

    const getDetailComment = async(page = "1") => {
        const response = await axios.get("/board/community/detail/comment",{
            params : {
                communitySeq : seq,
                page : page
            }
        })
        setDetailCommentData(response.data);
    }

    useEffect(()=>{
        const data = async()=>{
            try {
                await getDetail();
                await getDetailComment();
                setIsLoading(false);
                if (editorRef.current) {
                // 에디터 인스턴스에 접근해서 읽기 전용 모드로 변경
                editorRef.current.getInstance().setReadOnly(true);
                }
            } catch (error) {
                alert("정보를 불러오지 못햇습니다.")
                navigate("/board/community")
            }
        }

        data();
    },[])

     useEffect(()=>{
        const data = async()=>{
            try {
                await getDetail();
                await getDetailComment();
                setIsLoading(false);
                if (editorRef.current) {
                // 에디터 인스턴스에 접근해서 읽기 전용 모드로 변경
                editorRef.current.getInstance().setReadOnly(true);
                }
            } catch (error) {
                alert("정보를 불러오지 못햇습니다.")
                navigate("/board/community")
            }
        }

        data();
    },[seq])
    
    const changePage = (currentPage)=>{
        setCurrentPage(currentPage);
    }

    useEffect(()=>{
        setIsLoading(true);

        const data = async()=>{
            try {
                await getDetailComment(currentPage);
                setIsLoading(false);
            } catch (error) {
                alert("정보를 불러오지 못햇습니다.")
                navigate("/board/community")
            }
        }

        data();
    },[currentPage])


    const commentChange = (comment) =>{
        setCommentText(comment);
    }

    const writeComment = async() =>{

        if(token === null && token === "") {
            alert("로그인 후 가능합니다");
            return
        }


        if(!commentText.trim()) {
            alert("댓글을 입력해주세요");
            return;
        }


        try {
            const response = await axios.post("/board/community/comment/write",{
                postSeq : seq,
                content : commentText,
                author : nickname
            })
            alert(response.data);
            await getDetailComment();       
        } catch (error) {
            alert("작성 중 오류")
            navigate()
        }

    }

    const dto = detailData?.communityDetailDto ?? [];
    const recnetList = detailData?.recentTenCommunity ?? [];
    const commnetList = detailCommentData?.communityCommentList ?? [];
    const page = detailCommentData?.currentPage ?? [];

    if(isLoading) {
        return (
            <div>
                로딩중 입니다..
            </div>
        )
    }

    return (
        <div className={styles.detailBox} style={{textAlign}}>
            <div className={styles.recentTenBox}>
                <div className={styles.recentTenTextBox}>
                    Latest 10 list
                </div>
                <div>
                    {recnetList.map((list,index)=>(
                        <Link to={`/board/community/detail/${list.seq}`}>
                            <div key={list.seq} className={styles.recentTenBoardBox}>
                                <div className={styles.recentTenBoardView}>{list.views}</div>
                                <div>
                                    <div className={styles.recentTenBoardContentBox}>
                                        <div className={styles.recentTenBoardTitle}>{list.title}</div>
                                        <div className={styles.recentTenBoardCommentCount}>[{list.commentCount}]</div>
                                    </div>
                                    <div className={styles.recentTenBoardAuthor }>{list.nickname}</div>
                                </div>
                            </div>
                        </Link>
                    ))}
                </div>
            </div>
            <div className={styles.detailTuckBox}>
                <div className={styles.detailTitleBox}>
                    <div className={styles.detailTitleAlign}>
                        <div className="marginTop">
                            <span className={styles.detailTitle}>{dto.title}</span>
                        </div>
                        <div className={styles.detailTitleEtcBox}>
                            <div>{dto.nickname}</div>
                            <div>자유게시판</div>
                            <div>{dto.prettyTime}</div>
                            <div className={styles.detailTitleEtc}>
                                <div>views  {dto.views}</div>|
                                <div> comment  {dto.commentCount}</div>
                                {nickname === dto.nickname && (
                                <div>|  <button className={styles.detailUpdateButton}><Link to={`/board/community/update/${seq}`}>수정</Link></button></div>
                                )} 
                            </div>
                        </div>
                    </div>
                </div>
                <div className={styles.detailContentBox}>
                    <div style={{width:'95%'}}>
                        {dto.content && 
                            <Viewer
                                key = {dto.seq}
                                height = '400px'
                                initialValue={dto.content}
                                theme="dark"   // 다크 모드도 적용 가능
                            />                        
                        }
                    </div>
                </div>
                <div className={styles.detailCommentWriteBox}>
                    <div>
                        <form>
                            <div className="flex">
                                <div>   
                                    <textarea className={styles.detailCommentWriteContent} onChange={(e)=>{commentChange(e.target.value)}}></textarea>
                                </div>
                                <div>
                                    <button type="button" className={styles.detailCommentWriteContentBtn} onClick={()=>{writeComment()}}>댓글달기</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
                {Array.isArray(commnetList) && commnetList.length > 0 ? (
                    <div>
                        {commnetList.map((list,index)=>(
                            <CommunityDetailComment key={list.seq} seq={seq} comment={list} onWriteReplyComment={getDetailComment}></CommunityDetailComment>
                        ))}
                        <div className={styles.detailCommentPageBox}>
                            {Array.from({length : page.endPage - page.startPage + 1},(_,idx)=>{
                                const i = page.startPage + idx;
                                return(
                                        <div>
                                            <button
                                            key={i}
                                            onClick={()=>{ changePage(i)}}
                                            className = { `${i === page.page ? styles.active : ''} ${styles.pageButton} `}
                                            >
                                            {i}
                                            </button>
                                        </div>
                                )
                            })}
                        </div>
                    </div>
                ) : (
                    <div>
                        댓글이 없습니다..
                    </div>

                )}
            </div>
        </div>
    )
}

export default CommunityBoardDetail;