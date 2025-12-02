import { useParams } from "react-router-dom";
import { useState,useEffect } from "react";
import { useForm } from "react-hook-form";
import axios from "axios";
import styles from "../css/FindMemberBoardUpdate.module.css"
import { useNavigate } from "react-router-dom";

function FindMemberBoardUpdate() {
    const {seq} = useParams();
    const [detail,setDetail] = useState({}); 
    const [isLoading,setIsLoading] = useState(true);
    const navigate = useNavigate(); 

    const {
        register,
        handleSubmit,
        setValue
    } = useForm()

    const getDetail = async(seq)=> {
        const response = await axios.get('/board/findMemberBoard/page/update',{params : {
            seq : seq
        }})
        setDetail(response.data);
    }

    useEffect(()=>{
        const data = async() => {
            try {
                await getDetail(seq);
                setIsLoading(false)   
            } catch (error) {
                alert("정보를 불러오지 못햇습니다");
                navigate("/board/member");
            }
        }           
        
        data();
    },[])

    useEffect(()=>{
        setValue('title',detail.title);
        setValue('matchType',detail.matchType);
        setValue('content',detail.content);
    },[detail])

    const onsubmit = async (data) =>{
        try{
            const response = await axios.put(`/board/findMemberBoard/update/${seq}`,{
                ...data
            })
            alert(response.data);
            navigate(`/board/member/detail/${seq}`)
        } catch(error) {
            alert("수정 중 오류");
            navigate(`/board/member/detail/${seq}`)
        }
    }

    const deleteBoard = async () => {
        try {
            const response = await axios.delete(`/board/findMemberBoard/delete/${seq}`);
            alert(response.data);
            navigate('/board/member');
        } catch (error) {
            alert("삭제 중 오류")
            navigate(`/board/member/detail/${seq}`)
        }
    }

    if(isLoading) {
        return (
            <div>
                로딩중 입니다.....
            </div>
        )
    }

    return ( 
        <div className={styles.updateBox}>
            <form onSubmit={handleSubmit(onsubmit)}>
                <div className={styles.updateInputBox}>
                    <div className={styles.updateTitleBox}>
                        <div className={styles.updateTitleText}>
                            제목
                        </div>
                    </div>
                    <div>
                        <input
                            className={styles.findMemberBoardUpdateTitle}
                            {...register('title')}
                        />
                    </div>
                    <select
                        className={styles.findMemberBoardUpdateMatchType}
                        {...register('matchType')}
                    >
                        <option value="랭크" >랭크</option>
                        <option value="칼바람" >칼바람</option>
                        <option value="사용자 지정" >사용자 지정</option>
                        <option value="우르프">우르프</option>
                    </select>
                </div>
                <div>
                    <textarea 
                        rows={40}
                        className={styles.findMemberBoardUpdateContent}
                        {...register('content')}
                    />
                </div>
                <div className={styles.updateBtnBox}>
                    <button type="submit" className={styles.updateBtn}>수정</button>
                    <button type="button" className={styles.deleteBtn} onClick={()=>{deleteBoard()}}>삭제</button>
                </div>
            </form>
        </div>
    )
}

export default FindMemberBoardUpdate;