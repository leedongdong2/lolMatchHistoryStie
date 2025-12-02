import stlyes from '../css/signUp.module.css'
import { useForm } from 'react-hook-form';
import { useState,useEffect } from 'react';
import { useNavigate,useLocation,Link } from 'react-router-dom';
import axios from 'axios';
function signUp() {

    const naviget = useNavigate();
    const location = useLocation();
    const {googleId} = location.state || {};
    const [idCheck,setIdCheck] = useState(null);
    const [idCheckMessage,setIdCheckMessage] = useState(null);
    const [nicknameCheck,setNicknameCheck] = useState(null);
    const [nicknameCheckMessage,setNicknameCheckMessage] = useState(null);
    const [lolNameCheck,setLolNameCheck] = useState(null);
    const [lolNameCheckMessage,setLolNameCheckMessage] = useState(null);
    const [isReadOnly, setIsReadOnly] = useState(!!googleId);
    
    const {
        register,
        handleSubmit,
        formState : {error},
        watch,
        setValue
    } = useForm();

    useEffect(() => {
        if (googleId) {
            setValue('tnName', googleId, { shouldValidate: true });
            setIdCheck(true);
        }
    }, [googleId, setValue]);

    const id = watch('tnName');

    const checkIdAvailability = async (id)=> {
        const idRegex =   /^[a-zA-Z0-9]{5,16}$/;

        if(!idRegex.test(id)&&idCheck !== true) {
            setIdCheckMessage('아이디는 5~16자의 영문자 또는 숫자만 가능합니다.');
            return;
        }

        try {
            const response = await axios.get('user/signUp/idCheck',{
                params : {tnName : id}
            });
            const result = response.data;
            if(result === 0) {
                setIdCheck(true);
                setIdCheckMessage('사용가능한 아이디입니다.')
            } else {
                setIdCheckMessage('이미 사용중인 아이디입니다.')
            }
        } catch (error) {
            console.log(error);
            setIdCheckMessage('서버에서 오류가 발생햇습니다')
            setIdCheck(null);
        }
    }

    useEffect(()=>{
        setIdCheck(null);
        setIdCheckMessage(null);
    },[id])

    const nickname = watch('nickname');

    const checkNicknameAvailability = async (nickname)=>{
        try {

            const response = await axios.get('/user/signUp/nicknameCheck',{
                params : {nickname : nickname}
            });
            const result = response.data;
            if(result === 0) {
                setNicknameCheck(true);
                setNicknameCheckMessage('사용가능한 닉네임입니다');
            } else {
                setNicknameCheckMessage('이미 있는 닉네임입니다.')
            }
            
        } catch (error) {
            console.log(error);
            setNicknameCheckMessage('서버 오류')
            setNicknameCheck(null)
        }
    }

    useEffect(()=>{
        setNicknameCheck(null);
        setNicknameCheckMessage(null);
    },[nickname])

    const lolName = watch('lolName');
    const lolNametag = watch('lolNametag'); 
    const region = watch('region');

    const CheckLolNameAvailability = async (lolName,lolNametag,region)=>{

        const tagRegex = /^#.*/

        if(!tagRegex.test(lolNametag)) {
            setLolNameCheckMessage('태그앞에 #을 붙여주세요')
            return
        }

        try {

            const response = await axios.get('/user/signUp/lolNameCheck',{
                params :  {
                        lolName : lolName,
                        lolNametag : lolNametag,
                        region : region
                }
            })

            const result = response.data;
            setLolNameCheck(true);
            setLolNameCheckMessage(result);
            
        } catch (error) {
            if(error.response.status === 404){
                const text = error.response.data
                setLolNameCheckMessage(text);
                setLolNameCheck(null);
            }
                
        }
    }

    useEffect(()=>{
        setLolNameCheck(null);
        setLolNameCheckMessage(null);
    },[lolNametag,lolName,region])

    const signUpHandler = async (data)=>{
        if(idCheck === null) {
            alert('아이디 중복체크를 해주세요')
            return
        }
        if(nicknameCheck === null) {
            alert('닉네임 체크를 해주세요')
            return
        }
        if(lolNameCheck === null) {
            alert('롤 아이디체크를 해주세요')
            return
        }
        const response = await axios.post('/user/signUp',data)
        alert(response.data);
        naviget('/login')
    }

    return (
        <>
            <div className={stlyes.signUpTitlePosition}>
                <div className={stlyes.signUpTitleFont}>
                    <Link to="/">LDW.GG</Link>
                </div>
            </div> 
            <div className={stlyes.signUpBox}> 
                <form onSubmit={handleSubmit(signUpHandler)}>
                    <div className={stlyes.signUpInputBox}>
                        <div>회원가입</div>
                        <div className='flex'>
                            <div className={stlyes.signUpFontBox}> 
                                아이디 : 
                            </div>
                            <div>
                                <input
                                    type='text'
                                    {...register('tnName',
                                        {required : '아이디를 입력해주세요.'
                                        }
                                    )}
                                    readOnly={isReadOnly}
                                />
                                <p>{idCheckMessage}</p>
                            </div>
                            <div>
                            {!googleId && (  
                                <input type='button' className={stlyes.signUpIdCheckBox} value='중복체크' onClick={()=>{checkIdAvailability(id)}}/> 
                            )}
                            </div>
                        </div>
                         {!googleId && (
                                <div className='flex'>
                                    <div className={stlyes.signUpFontBox}>
                                        비밀번호 : 
                                    </div>
                                    <div>
                                        <input 
                                            type='password'
                                            {...register('tnPassword',{
                                                required : '비밀번호를 입력해주세요',
                                                pattern : {
                                                    value : /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/,
                                                    message : '비밀번호는 8~20자, 영문+숫자+특수문자를 모두 포함해야 합니다.'
                                                }
                                            })}
                                        />
                                    </div>
                                </div>
                            )}
                        <div className='flex'>
                            <div className={stlyes.signUpFontBox}>
                                닉네임 : 
                            </div>
                            <div>
                                <input 
                                    type='text'
                                    {...register('nickname',{
                                        required : '별명을 입력해주세요'
                                    })}
                                />
                            </div>
                            <div>
                                <input type='button' value='중복체크' className={stlyes.nickNameCheckBox} onClick={()=>{checkNicknameAvailability(nickname)}}/>
                                <div>{nicknameCheckMessage}</div>
                            </div>
                        </div>
                        <div className='flex'>
                            <div className={stlyes.signUpFontBox}>
                                국가 :  
                            </div>
                            <div>
                                <select {...register('region',{
                                    required : '국가를 선택해주세요'
                                })}>
                                    <option value='asia/kr'>kr</option>
                                    <option value='america'>na</option>
                                </select>
                            </div>
                        </div>
                        <div className='flex'>
                            <div className={stlyes.signUpNicknameFontBox}>
                                LOL닉네임 :
                            </div>
                            <div>
                                <input
                                    type='text'
                                    {...register('lolName',{
                                        required : '롤닉네임을 입력해주세요'
                                    })}
                                />
                                <input
                                    type='text'
                                    {...register('lolNametag',{
                                        required : '롤태그를 입력해주세요',
                                        pattern : {
                                            value : /^#.*/,
                                            message : '태그 앞에 #을 꼭 붙여주세요'
                                        }
                                    })}
                                    placeholder='#'
                                />
                                <p>{lolNameCheckMessage}</p>
                            </div>
                            <div>
                                <input type='button' className={stlyes.lolNameCheckBox} value='닉네임체크' onClick={()=>{CheckLolNameAvailability(lolName,lolNametag,region)}}/>
                            </div>
                        </div>
                        <div>
                            <input type='submit' value='signUp' className={stlyes.signUpButton}/>
                        </div>
                    </div>
                </form>
            </div>
        </>
    )
}

export default signUp;