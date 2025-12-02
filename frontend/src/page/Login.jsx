import axios from "axios";
import styles from "../css/loginPage.module.css"
import { useForm } from "react-hook-form";
import { Link, useNavigate } from 'react-router-dom';
import { GoogleOAuthProvider,GoogleLogin } from '@react-oauth/google';
import {useUser} from './reactContext/LoginUserContext.jsx';

function Login() {

const navigate = useNavigate();
const { setUserData } = useUser();

const {
    register,
    handleSubmit,
    formState : {errors}
} = useForm();

const login = async (data) => {
    const response = await axios.post('user/api/login',data);
    const token = response.data.token;
    localStorage.setItem('token',token);
    return token
}

const getLoginUserInfo = async (token) => {
    const response = await axios.get('riot/login/user',{
        headers :{
            Authorization : `Bearer ${token}`
        }
    });
    const userData = response.data;
    return userData;
}



const onSubmit = async (data) => {
    try {
        const token = await login(data);
        const userData = await getLoginUserInfo(token);
        setUserData(userData);
        navigate('/');
    } 
    catch (error) {
        console.log(error);
    }
}


    const handleSuccess  = async (credentialResponse) => {
        try {
            //구글이 보내준  credential(token 또는 authorization code)
            const token = credentialResponse.credential;
            const response = await axios.post('/user/api/google',{token});
            if(response.data.status === 'notUser') {
                navigate('/signUp',{state:{googleId : response.data.googleId}})
                return;
            }
            //받아온 jwt 토큰 저장
            localStorage.setItem('token',response.data.token);
            const userData = await getLoginUserInfo(response.data.token);
            setUserData(userData);
            navigate('/');

        } catch (error) {
            console.log('구글 로그인 실패 ',error);
        }
    }


    return (
        <div>
            <div className={styles.loginPageHeader}>
                <Link to="/">LDW.GG</Link>
            </div>
            
            <div className={styles.loginPageMain}>
                <div className={styles.loginBox}>
                    <span className={styles.loginFont}>로그인</span>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div>
                            <input
                            type="text"
                             {...register("username",{
                                required : "아이디를 입력해주세요."
                             })}
                             placeholder="아이디를 입력해주세요"
                            />
                        </div>
                        <div>
                            <input
                            type="password" 
                            {...register('password',{
                                required : "비밀번호를 입력해주세요"
                            })}
                            placeholder="비밀번호를 입력해주세요"
                            />
                        </div>
                        <div>
                            <input type="submit" value='Login'></input>
                        </div>
                    </form>
                <div>
                    아이디가 없으신가요? <Link to="/signUp">회원가입</Link>
                </div>  
                <GoogleOAuthProvider clientId="1055793807837-8m64369epiae7tno6q6ehi7vd8noicdk.apps.googleusercontent.com">
                    <GoogleLogin
                        onSuccess={handleSuccess}
                        onError={()=>{
                            console.log('Login Failed');
                        }}
                    />
                </GoogleOAuthProvider>
                </div>
            </div>
        </div>
    )
}

export default Login;