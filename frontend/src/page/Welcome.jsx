import { useState,useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import {useForm,Controller} from 'react-hook-form';

function Welcome({name}) {

    const [count,setCount] = useState(0);
    const [message,setMessage] = useState({});
    const [message1,setMessage1] = useState({});
    const navigate = useNavigate();

    useEffect(()=>{
        axios.get('/react/hello')
        .then((response)=>{
            setMessage(response.data);
        })
        .catch((error)=>{
            console.log("데이터 가져오기 실패",error);
        });
    },[]);

    const {
        control,
        handleSubmit : handleSubmit1,
        setValue,
        formState : {errors : errors1}
    } = useForm();

    useEffect(() => {
    setValue('count', count);  // 외부 count가 바뀔 때마다 react-hook-form에 반영
    }, [count, setValue]);



    const {
        register,
        handleSubmit,
        formState : {errors}
    } = useForm();

    const onSubmit = (data) => {
        navigate('/link',{ state:data });
    }


    const onSubmit1 = (data) => {
        console.log("폼 데이터:", data);
        axios('/react/test',{
            params : data
        })
        .then((response)=>{
            setMessage1(response.data);
        })
        .catch((error)=>{
            console.log("실패유")
        })
    }
    
    const increaseCount = () => {
        const newCount = count + 1;
        setCount(newCount);
        setValue('count',newCount);
    }


   return (
        <div> 
            <h2>Welcome,{name}!</h2>
            <div>
                <p>현재숫자 : {count}</p>
                <button onClick={increaseCount}>+1</button>
            </div>
            <div>
                {message.name}
                {message.age}
                {message.isMember}
            </div>

            <div>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <input type="text" placeholder='dd' 
                    {...register('name',{required : '값을 입력해주세요'})}
                    />
                    {errors.name && <p style={{ color: 'red' }}>{errors.name.message}</p>}
                    <button type='submit'>전송</button>
                </form>
            </div>

            <div>
                <form onSubmit={handleSubmit1(onSubmit1)}>
                    <Controller 
                        name="count"
                        control={control}
                        defaultValue={count}
                        render={({field}) => 
                           <input type='text'
                           value={count}
                           onChange={(e)=>{
                             const value = e.target.value
                             setCount(value);
                             field.onChange(value);
                           }}
                           />
                        }
                    />
                    <button type='submit'>전송</button>
                </form>
                <div>
                    {message1.count}
                </div>
            </div>

        </div>
   );
            
}

export default Welcome;