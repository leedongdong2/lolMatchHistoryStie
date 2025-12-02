import styles from '../css/findMeberBoardWrite.module.css';
import { useForm } from 'react-hook-form';
import { useUser } from './reactContext/LoginUserContext'; 
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function FindMeberBoardWrite() {
    const {nickname,userId} = useUser();
    const navigate = useNavigate();
    
    const {
        register,
        handleSubmit,
        formState : {errors}
    } = useForm();

    const onSubmit = async(data) => {
        try {
            const response = await axios.post('/board/findMemberBoard/write',{
                ...data,
                tnName : userId,
                nickname : nickname,
            })
            alert(response.data);
            navigate('/board/member');
        } catch (error) {
            alert("작성 중 오류")
            navigate('/board/member');
        }
    }
    return ( 
        <div className={styles.writeBox}>
            <form onSubmit={handleSubmit(onSubmit)}>
                <div className={styles.writeInfoBox}>
                    <div className={styles.findMemberBoardWriteTitleTextBox}>
                        <div className={styles.writeTitleText}>
                            제목
                        </div>
                    </div>
                    <div>
                        <input
                         className={styles.findMemberBoardWriteTitle}
                         {...register('title')}
                        />
                    </div>
                    <select 
                        className={styles.findMemberBoardWriteMatchType}
                        {...register('matchType')}>
                        <option value="랭크">랭크</option>
                        <option value="칼바람">칼바람</option>
                        <option value="사용자 지정">사용자 지정</option>
                        <option value="우르프">우르프</option>
                    </select>
                </div>
                <div>
                    <textarea
                        className={styles.findMemberBoardWriteContent}
                        rows={40} 
                        {...register('content')}
                    />
                </div>
                <div className={styles.findMemberBoardWriteBtnBox}>
                    <button type='submit' className={styles.writeButton}>제출</button>
                </div>
            </form>
        </div>
    )
}

export default FindMeberBoardWrite;