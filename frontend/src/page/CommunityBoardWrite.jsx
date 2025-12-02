import { useRef } from "react";
import { useForm } from "react-hook-form";
import { Editor } from '@toast-ui/react-editor';
import '@toast-ui/editor/dist/toastui-editor.css';
import '@toast-ui/editor/dist/theme/toastui-editor-dark.css'; 
import styles from "../css/communityWrite.module.css"
import { useState } from "react";
import {useUser} from './reactContext/LoginUserContext.jsx'
import axios from "axios";
import { useNavigate } from "react-router-dom";

function CommunityBoardWrite() {
    const [contentTitle,setContentTitle] = useState("");
    const {nickname,userId} = useUser();
    const navigate = useNavigate();

    const editorRef = useRef();

    
    const onSubmit = async()=>{
        try {

            const content = editorRef.current.getInstance().getMarkdown();

            const response = await axios.post("/board/community/write",{
                title : contentTitle,
                content : content,
                nickname : nickname,
                tnName : userId
            });

            alert(response.data);
            
            navigate("/board/community");
            
        } catch (error) {
            alert("작성 중 오류.");
            navigate("/board/community");
        }
    }

    return (
        <div className={styles.writeBox}>
            <form>
                <div className={styles.writeInfoBox}>
                    <div className={styles.communityWriteTitleTextBox}>
                        <div className={styles.writeTitleText}>
                            제목
                        </div>
                    </div>
                    <div>
                        <input type="text" className={styles.communityWriteTitle} onChange={(e)=>{setContentTitle(e.target.value)}}/>
                    </div>                    
                </div>
                <div className={styles.communityWriteContentBox}>
                    <div>
                        <Editor
                            ref={editorRef}
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
                    </div>    
                    <input type="button" value="작성하기" className={styles.writeButton} onClick={()=>{onSubmit()}} ></input>
                </div>
            </form>
        </div>
    )
    
}


export default CommunityBoardWrite;