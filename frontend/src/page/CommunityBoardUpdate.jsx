import { useParams } from "react-router-dom";
import { useState,useEffect,useRef } from "react";
import axios from "axios";
import styles from "../css/communityUpdate.module.css";
import { Editor } from '@toast-ui/react-editor';
import '@toast-ui/editor/dist/toastui-editor.css';
import '@toast-ui/editor/dist/theme/toastui-editor-dark.css'; 
import { useNavigate } from "react-router-dom";

function CommunityBoardUpdate() {
    const [detailData,setDetailData] = useState("");
    const [title,setTitle] = useState("");
    const editorRef = useRef();
    
    const {seq} = useParams();

    const navigate = useNavigate();
     
    useEffect(()=>{
        const data = async () => {
            try {
                const response = await axios.get("/board/community/updateDetail",{
                    params : {
                        communitySeq : seq
                    }
                })
                setDetailData(response.data);
                setTitle(response.data.title);
            } catch (error) {
                alert("정보를 불러오지 못햇습니다.")
                navigate("/board/community")
            }
        }
        data();
    },[])
    
    const onSubmit =  async() => {
        try {
            const content = editorRef.current.getInstance().getMarkdown();

                if(!title.trim()) {
                    alert("제목을 입력해주세요");
                    return;
                }

                if(!content.trim()) {
                    alert("본문을 입력해주세요");
                    return;
                }
                
                const response = await axios.put(`/board/community/updateCc/${seq}`,{
                    content : content,
                    title : title
                })

                alert(response.data);
                navigate(`/board/community/detail/${seq}`)
        } catch (error) {
            alert("수정에 실패하였습니다.")
            navigate(`/board/community/detail/${seq}`)
        }
    }

    const deleteBoard = async()=>{
        try {
            const response = await axios.delete(`/board/community/deleteCc/${seq}`)
            alert(response.data);
            navigate("/board/community");
        } catch (error) {
            console.log(error);
            navigate(`/board/community/detail/${seq}`)
        }
    }
    return (
        <div className={styles.updateBox}>
            <div className={styles.updateInputBox}>
                <div className={styles.updateTitleBox}>
                    <div className={styles.updateTitleText}>
                        제목
                    </div>
                </div>
                <div className={styles.communityUpdateTitleBox}>
                    <input type="text" value={title} onChange={(e)=>{setTitle(e.target.value)}} style={{all:"unset"}}/>
                </div>
            </div>
            <div className={styles.communityUpdateContentBox}>
            <div>
                {detailData.content && (
                        <Editor
                            ref={editorRef}
                            initialValue = {detailData?.content}
                            previewStyle="tab"
                            height="600px"
                            initialEditType = "wysiwyg"
                            useCommandShortcut={true}
                            hideModeSwitch={true}

                            theme = "dark"
                            hooks={{
                                    addImageBlobHook : async (img,callback) => {
                                    const formData = new FormData();
                                    formData.append('file',img);

                                    const response = await axios.post('/board/upload/image',formData);
                                    const imageUrl = response.data;
                                    callback(imageUrl, 'alt text');
                                    }
                            }}
                        />
                )} 
            </div>
            <div className={styles.updateBtnBox}>
                <input  className={styles.updateBtn} type="button" value="수정" onClick={()=>{onSubmit()}}/>
                <input  className={styles.deleteBtn} type="button" value="삭제" onClick={()=>{deleteBoard()}}/>
            </div>
            </div>
        </div>
    )
}

export default CommunityBoardUpdate;