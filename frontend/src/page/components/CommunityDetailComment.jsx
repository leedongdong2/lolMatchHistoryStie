import { useState,useEffect } from "react";
import styles from "../../css/communityDetail.module.css"
import { useUser } from "../reactContext/LoginUserContext";
import axios from "axios";

function CommunityDetailComment({seq,comment,onWriteReplyComment}) {

    const [updateVisible,setUpdateVisible] = useState(false);
    const [writeVisible,setWriteVisible] = useState(false);
    const {nickname} = useUser();
    const [commentContent,setCommentContent]  = useState("");
    const [replyCommnetContent,setReplyCommentContent] = useState(comment.content);

    const token = localStorage.getItem("token");

    const writeBoxVisibleToggle = ()=>{
        if(token === null) {
            alert("로그인을 먼저 해주세요.");
            return
        } 
        setWriteVisible( prev => !prev);
    }

    const updateBoxVisibleToggle = ()=>{
        setUpdateVisible(prev => !prev);
    }

    const chageCommentText = (comment)=> {
            setCommentContent(comment);
    }

    const chageReplyCommentText = (comment) => {
        setReplyCommentContent(comment);
    }



    const writeComment = async () => {

        if(!commentContent.trim()){
            alert("댓글을 입력해주세요")
            return;
        }

        try {
                const response = await axios.post("/board/community/comment/write",{
                    postSeq : seq,
                    parentSeq : comment.seq,
                    content : commentContent,
                    author : nickname
                    }
                )
            alert(response.data);
            await onWriteReplyComment?.();
            setCommentContent("");
            setWriteVisible(false);
        } catch (error) {
            alert("작성 중 오류");
            navigate(`/board/community/detail/${seq}`)
        }
    }

    const updateComment = async()=>{

        if(!replyCommnetContent.trim()){
            alert("댓글을 입력해주세요");
            return
        }

        try {
            const response = await axios.put(`/board/community/comment/update/${comment.seq}`,{
                content : replyCommnetContent
            })
            alert(response.data);
            setUpdateVisible(false);
            await onWriteReplyComment?.();
        } catch(error) {
            alert("수정 중 오류");
            navigate(`/board/community/detail/${seq}`)
        }
    }

    const deleteComment = async() => {
        try {
            const response = await axios.delete(`/board/community/comment/delete/${comment.seq}`)
            alert(response.data);
            await onWriteReplyComment?.();
        } catch (error) {
            alert("삭제 중 오류");
            navigate(`/board/community/detail/${seq}`)
        }
    }

    return (
        <>
            <div style={{marginLeft:`${comment.depth * 1}vw`, minHeight:"11vh",backgroundColor:"#23272b",color:"rgba(240, 248, 255, 0.514)"}}>
                <div className={styles.communityCommentBox}>
                    <div className={styles.communityCommentInfo}>
                        <strong>{comment.author}</strong>
                        <span> | {comment.prettyTime}</span>
                    </div>
                    <div className={styles.communityComment}>{comment.content}</div>
                    <div className="flex">
                        <div className={styles.toggleReplyForm} onClick={()=>{writeBoxVisibleToggle()}}>답글달기</div>
                        {nickname === comment.author && (
                            <div className={styles.communityCommentButtons}>
                                <span style={{cursor:"pointer"}} onClick={()=>{updateBoxVisibleToggle()}}>수정</span>
                                <span style={{cursor:"pointer"}} onClick={()=>{deleteComment()}}> | 삭제</span>
                            </div>
                        )}
                    </div>
                    <div className={styles.communityReplyFormBox} style={{display : writeVisible ? "block" : "none"}}>
                        <form>
                            <div className="flex">
                                <div>
                                    <textarea className={styles.replyCommentContent} value={commentContent} onChange={(e)=>{chageCommentText(e.target.value)}}></textarea>
                                </div>
                                <div>
                                    <button type="button" className={styles.replyCommentBtn} onClick={()=>{writeComment()}}>작성</button>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div className={styles.communityReplyFormBox} style={{display : updateVisible ? "block" : "none"}}>
                        <form>
                            <div className="flex">
                                <div>
                                    <textarea className={styles.replyCommentContent} value={replyCommnetContent} onChange={(e)=>{chageReplyCommentText(e.target.value)}}></textarea>
                                </div>
                                <div>
                                    <button type="button" className={styles.replyCommentBtn} onClick={()=>{updateComment()}}>수정</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            {/**댓글에 자식 댓글이 잇다면 계속 불러온다(재귀함수) **/}
            {comment.children && comment.children.length > 0 && (
                <div>
                    {comment.children.map((child)=>(
                        <CommunityDetailComment key={child.seq} seq={seq} comment={child} onWriteReplyComment={onWriteReplyComment}></CommunityDetailComment>  
                    ))}
                </div>
            )}
        </>
    )
}

export default CommunityDetailComment;