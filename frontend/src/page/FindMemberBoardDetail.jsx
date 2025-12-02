import { useParams } from "react-router-dom";
import { useState,useEffect } from "react";
import axios from "axios";
import styles from "../css/findMemberBoardDetail.module.css";
import { useUser } from "./reactContext/LoginUserContext";
import RiotTooltip from "./components/RiotTooltip";
import { useForm } from "react-hook-form";
import { Link } from "react-router-dom";



function FindMemberBoardDetail() {
    const [detail,setDetail] = useState({});
    const [detailComment,setDetailComment] = useState({});
    const {nickname} = useUser();
    const [isLoading,setIsLoading] = useState(true);
    const [currentPage,setCurrentPage] =useState(1);
    const [styleVisible,setStyleVisible] = useState([]);
    const [commentContent,setCommentContent] = useState("");
    const token = localStorage.getItem("token");

    const {seq} = useParams();

    const getDetail = async(seq)=> {
        const response = await axios.get('/board/findMemberBoard/detail',{params : {
            seq : seq
        }})
        setDetail(response.data);
    }

    const getDetailComment = async(seq,page = '1') => { 
        const response = await axios.get('/board/findMemberBoard/detail/comment',{
            params : {
                seq : seq,
                page : page
            }
        })
        setDetailComment(response.data);
    }

    useEffect(()=>{
    const data = async ()=> {
        try {
         await getDetail(seq);
         await getDetailComment(seq);
         setIsLoading(false);
        } catch (error) {
            alert("정보를 불러오지 못햇습니다.");
            navigate("/board/member")
        }
    };

    data();
    },[])


    useEffect(()=>{
    const data = async ()=> {
        try {
         await getDetail(seq);
         await getDetailComment(seq);
         setIsLoading(false);
        } catch (error) {
            alert("정보를 불러오지 못햇습니다.");
            navigate("/board/member")
        }
    };

    data();
    },[seq])

    const changePage = (currentPage)=> {
        setCurrentPage(currentPage);
    }

    useEffect(()=>{
        setIsLoading(true)
        
        const data = async () => {
            try {
               await getDetailComment(seq,currentPage);
               setIsLoading(false);
            } catch (error) {
                alert("정보를 불러오지 못햇습니다");
                navigate("/board/member")
            }
        }

        data();
    },[currentPage])

    useEffect(()=>{
        const diff = detailComment?.findMemberBoardDetailComment ?? [];
        setStyleVisible([...Array(diff.length).fill(false)]);
    },[detailComment])


    const {register,handleSubmit,setValue} = useForm();

    const updataComment = async (data,commentSeq,currentPage) => {
        try {
            const content = data[`content_${commentSeq}`]
            const response = await axios.put(`/board/findMemberBoard/comment/update/${commentSeq}`,{content : content})
            alert(response.data);
            getDetailComment(seq,currentPage);            
        } catch (error) {
            error("수정 중 오류")
            navigate(`/board/member/detail/${seq}`) 
        }

    }

    const deletComment = async (commentSeq,currentPage)=> {
        try {
            const response = await axios.delete(`/board/findMemberBoard/commnet/delete/${commentSeq}`)
            alert(response.data);
            getDetailComment(seq,currentPage);        
        } catch (error) {
            error("삭제 중 오류")
            navigate(`/board/member/detail/${seq}`) 
        }
    }


    const visibleToggleCommentUpdateBox = (index,comment)=> {
        setStyleVisible(prev => {
            const toggle = [...prev];
            toggle[index] = !toggle[index];
            return toggle
        });

        setValue(`content_${comment.seq}`, comment.content)
    }

    const writeComment = async ()=> {

        if(token === null) {
            alert("로그인 후 이용해주세요");
            return;
        }

        if(commentContent === "") {
            alert("공백은 입력이 안됩니다");
            return 
        }
        
        try {
            const response = axios.post('/board/findMemberBoard/comment/write',{
                    seq : seq,
                    nickname : nickname,
                    content : commentContent
                }
            )

            alert(response.data);
            setCommentContent("");
            getDetailComment(seq);
        } catch (error) {
            alert("작성 중 오류")
            navigate(`/board/member/detail/${seq}`)

        }
    }

    const dto = detail?.findMemberBoardDto ?? [];
    const recentList  = detail?.recentTenFindMemberBoard ?? [];
    const commentList = detailComment?.findMemberBoardDetailComment ?? [];
    const page = detailComment?.page ?? [];


    if(isLoading || !detailComment.page) {
        return (
            <div>
                로딩중 입니다..
            </div>
        )
    }
    
        return (
            <div>
                <div className={styles.detailBox}>
                    <div className={styles.recentTenBox}>
                        <div className={styles.recentTenTextBox}>
                            Latest 10 list 
                        </div>
                        <div>
                                {recentList.map((list,index)=>(
                                    <Link to={`/board/member/detail/${list.seq}`}>
                                        <div className={styles.recentTenBoardBox} key={list.seq}>
                                            <div className={styles.recentTenBoardView}>
                                                {list.views}
                                            </div>
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
                    <div className={styles.detailTuckBox}>
                        <div className={styles.detailTitleBox}>
                            <div className={styles.detailTitleAlign}>
                                <div className="marginTop">
                                    <spna className={styles.detailTitle}>{dto.title}</spna>
                                </div>
                                <div className={styles.detailTitleEtcBox}>
                                    <div>{dto.nickname}</div>
                                    <div>매칭듀오</div>
                                    <div>{dto.prettyTime}</div>
                                    <div>{dto.matchType}</div>
                                    <div className={styles.detailTitleEtc}>
                                        <div>views {dto.views}</div>|
                                        <div>comment {dto.commentCount}</div>|
                                        {nickname === dto.nickname && (
                                            <div>
                                                <button className={styles.detailUpdateButton}><Link to={`/board/member/update/${seq}`}>수정</Link></button>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className={styles.detailContentBox}>
                            <div className={styles.detailContent}><RiotTooltip rawHtml={dto.content}></RiotTooltip></div>
                        </div>
                        <div className={styles.detailCommentWriteBox}>
                                <form>
                                    <div className='flex'>
                                        <div>
                                            <textarea className={styles.detailCommentWriteContent} value={commentContent} required onChange={(e)=>{setCommentContent(e.target.value)}} onKeyDown={(e)=>{if(e.key === 'Enter') { if(e.shiftKey) {return} {writeComment()}}} }></textarea>
                                        </div>
                                        <div>
                                            <input type="button" className={styles.detailCommentWriteContentBtn} value='댓글달기' onClick={()=>{writeComment()}} ></input>
                                        </div>
                                    </div>
                                </form>
                        </div>
                        {Array.isArray(commentList) && commentList.length === 0 ? (
                            <div>
                                댓글이 없습니다...
                            </div>
                        ) : (
                            commentList.map((list,index)=>(
                                <div key={list.seq}>
                                    <div className={styles.findMemberBoardDetailCommentBox}>
                                        <div className={styles.findMemberBoardDetailCommentInfo}>
                                            <span>{list.nickname}  </span>
                                            <span>{list.prettyTime}</span>
                                        </div>
                                        <div className={styles.findMemberBoardDetailComment}>{list.content}</div>
                                        {list.nickname === nickname && (
                                            <div className={styles.findMemberBoardDetailCommentButtons}>
                                                <span className={styles.findmemberBoardDetailCommentBoxBtn} onClick={()=>{visibleToggleCommentUpdateBox(index,list)}}> 수정</span> |
                                                <span className={styles.findmemberBoardDetailCommentBoxBtn} onClick={()=>{deletComment(list.seq,currentPage)}}> 삭제</span>
                                            </div>
                                        )}
                                    </div>
                                    <div className={styles.updateFindmemberBoardDetailCommentBox} style={{display : styleVisible[index] ? 'block' : 'none'}}>
                                        <div className={styles.updateFindmemberBoardDetailComment}>
                                            <form onSubmit={handleSubmit((data)=> updataComment(data,list.seq,currentPage))}>
                                                <div className='flex'>
                                                    <div>
                                                        <textarea className={styles.updateFindmemberBoardDetailCommentContent}
                                                         {...register(`content_${list.seq}`,{required : '공백은 수정할수없습니다'})}
                                                        ></textarea>
                                                    </div>
                                                    <div>
                                                        <button type="submit" className={styles.updateFindmemberBoardDetailCommentBtn}>수정</button>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                        <div className={styles.detailCommentPageBox}>
                        {Array.from({length : page.endPage - page.startPage + 1},(_,idx)=>{
                            const i = page.startPage + idx;
                            return(
                                    <div>
                                        <button
                                        key={i}
                                        onClick={()=>{changePage(i)}}
                                        className={`${styles.pageButton} ${i === page.page ? styles.active : ''}`}
                                        >
                                        {i}
                                        </button>
                                    </div>
                            )
                        })}
                        </div>

                    </div>
                </div>  
             </div>
        )
}

export default FindMemberBoardDetail;