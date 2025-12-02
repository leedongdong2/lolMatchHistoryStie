import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {useForm} from 'react-hook-form';
import styles from '../css/home.module.css';


function Home() {

    const [championNames,setChampionNames] = useState([]);
    // useState : react 에서 컴포넌트의 상태를 관리하기위한 훅 값이 바뀔떄마다 자동으로 렌더링이 되어 화면이 바뀐다
    // react에서는 이처럼 상태관리인 useState의 값을 변화하여 화면을 바꿈
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    //태그에 Link를 쓰는거와 같이 함수에서 navigate로 페이지를 이동시킨다  


    useEffect(()=>{
        axios('/riot/rotation')
        .then((response)=>{
            setChampionNames(response.data.rotationChampionsNames);
        })
        .catch((error)=>{
            console.log("오류가 낫습니다")
            setError("오류")
        });
    },[]);
    //useEffect 컴포넌트가 렌더링된 이후 실행되는 훅이다.
    //의존성 배열의 값이 바뀔떄마다 실행된다 
    //만약 championNames의 값이 바뀔떄마다 어떤 이벤트를 계속해야한다면 [championNames] 처럼 의존성 배열에 넣으면 됨
    //[]처럼 빈값은 처음 마운트 될떄 한번만 실행하겠다는 뜻이다.
    //아예 의존성 배열을 하지않으면 이상한 동작을 하거나 무한으로 돌아간다
    //async함수를 쓸떄는 직접 쓸수 없고 const data = async ()=>{} 처럼 안에 함수를 넣어 실행시켜주자 





       const {
        register,//{...register} form을 구성할떄 폼안의 태그들의 네임,옵션(required,pattern)등을 설정하는 부분
        handleSubmit,//폼을 제출하는 이벤트를 할떄 옵션들의 체크, 태그들의 벨류 수집을 해준다
        formState : {errors}//제출할떄 에러가 나면 에러메시지를 알려준다
    } = useForm();

    const onSubmit = (data) => {
        navigate('/find',{state:data});
    }

    if(error) return <div>{error}</div>;




    return (
            <div className={styles.mainBox}>
                <div className={styles.mainSearchBox}>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <select className={styles.mainSearchRegionSelect}
                            {...register("region")}
                        >
                            <option value="asia/kr">kr</option>
                        </select>
                        <input 
                            type="text" 
                            className={styles.mainSearchInput}
                            placeholder={errors.riotId ? '' : 'Riot ID#태그'}
                        {...register("riotId",{
                            required : '값을 입력해주세요',
                            pattern : {
                                value : /^(?=.*#).+$/,
                                message : '#을 포함해야합니다'
                            }
                        })}
                        />
                        {errors.riotId && (
                            <span className={styles.insideError}>{errors.riotId.message}</span>
                        )}
                        <input type="submit" value=".GG" className={styles.mainSearchButton}/>
                    </form>
                </div>  
                <div className={styles.mainRotationChampionImg}>
                    {championNames.map((name,index)=>(
                        <div key={index} style={{width : 'calc(100% / 10)', padding: '4px', boxSizing : 'border-box'}}>
                            <img
                                src={`https://ddragon.leagueoflegends.com/cdn/15.8.1/img/champion/${name}.png`}
                                alt={name}
                                style={{width: '100%', height: 'auto'}}
                            />
                        </div>
                    ))}
                </div>
            </div>
    )
}

export default Home;