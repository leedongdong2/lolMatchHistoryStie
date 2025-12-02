import axios from "axios";
import React,{useEffect,useState} from "react"
import qs from "qs";

import { Line } from 'react-chartjs-2';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

// Chart.js에 필요한 컴포넌트 등록하기
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ChartDataLabels
);



function EtcViewPanal({userData,matchId,participant}) {
    const[data,setData] = useState({});
    const[isLoading,setIsLoading] = useState(true);

    useEffect(()=>{
        axios.get('/riot/matchInfo/etc',{
            params : {...userData,
                    riotMatchId : matchId,
                    participantId : participant
            },
            paramsSerializer : (params) => qs.stringify(params,{encode:true})
        })
        .then(response=>{
            setData(response.data);
            setIsLoading(false);
        })
        .catch(error=>{
            console.log(error);
            setIsLoading(false);
        })
    },[])

    const intervals = data.intervals;
    const csList = data.csList;
    const goldList = data.goldList;


    const chartData = {
    labels: intervals,
    datasets: [
      {
        label: '총 골드량',
        data: goldList,
        borderColor: 'gold',
        backgroundColor: 'rgba(255, 215, 0, 0.2)',
        yAxisID: 'goldAxis',
        tension: 1.0,
      },
      {
        label: 'CS 수',
        data: csList,
        borderColor: 'skyblue',
        backgroundColor: 'rgba(135, 206, 250, 0.2)',
        yAxisID: 'csAxis',
        tension: 0.4,
      },
    ],
  };

  const options = {
    maintainAspectRatio: false,
    responsive: true,
    plugins: {
      title: {
        display: true,
        text: '시간별 골드 & CS',
        font: { size: 18 },
      },
      legend: {
        display: true,
      },
      datalabels: {
        display: true,
        color: 'blue',
        font: {
          weight: 'bold',
        },
        formatter: function (value, context) {
          return value;
        },
      },
    },
    scales: {
      x: {
        title: {
          display: true,
          text: '시간 (분)',
          font: { size: 14 },
        },
        ticks: {
          callback: function (value, index) {
            return intervals[index] * 4 + '분';
          },
        },
      },
      goldAxis: {
        type: 'linear',
        position: 'left',
        title: {
          display: true,
          text: '총 골드량',
        },
      },
      csAxis: {
        type: 'linear',
        position: 'right',
        title: {
          display: true,
          text: 'CS 수',
        },
        grid: {
          drawOnChartArea: false,
        },
      },
    },
  };

    
    if(isLoading) {
        return (
            <div>
                로딩중입니다잉
            </div>
        )
    }



    return (
       <div style={{width :'50vw',height:'60vh'}}>
       <Line data={chartData} options={options} />;
       </div>
    )
}

export default React.memo(EtcViewPanal);