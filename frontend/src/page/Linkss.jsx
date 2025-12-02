import axios from 'axios';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
function Linkss() {
    const location = useLocation();
    const data =location.state;
    const [test,setTest] = useState({});

    useEffect(()=>{
        axios.get('/react/link', {
            params : data
        })
        .then((response)=>{
            setTest(response.data);
        });

    },[]);

    return (
        <div>
            <Link to="/">{test.name}</Link>
            <div>하하하하하</div> 
        </div>
    )
}

export default Linkss;