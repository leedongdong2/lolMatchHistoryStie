import axios from "axios";
import { useState,useEffect,react } from "react"
import { 
 DndContext,
 pointerWithin,
 PointerSensor,
 useSensor,
 rectIntersection,
 useSensors,
 useDroppable,
 DragOverlay
} from '@dnd-kit/core';//드래그앤드롭이벤트 키트 라이브러리
import { 
    SortableContext,
    arrayMove,
    verticalListSortingStrategy,
    useSortable
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';


function TierList() {

    // 초기 티어 상태 설정
    const initialTiers = [              //items <-여기에 이제 이미지 url이 들어감
        {id : 'op', title : 'op챔', items : []},
        {id : '1티어', title : '1티어챔', items : []},
        {id : '2티어', title : '2티어챔', items : []},
        {id : '3티어', title : '3티어챔', items : []},
        {id : '4티어', title : '4티어챔', items : []}
    ]
    //챔피언 목록(pool) 티어박스에 넣기전 이미지들
    const [champData,setChampData] = useState([]);
    // 선택한 포지션
    const [position,setPosition] = useState("");
    //티어 정보                        기본값 설정
    const [tiers,setTiers] = useState(initialTiers);
    //움직이는 컴포넌트
    const [activeId, setActiveId] = useState(null);

    
    // 마우스 포인터 감지 센서 설정
    const sensors = useSensors(useSensor(PointerSensor));


    // 챔피언 id가 어느 티어 박스에  속해잇는지 찾아준다
    const findItemInTiers = (id) => {
        for (const tier of tiers) {
                //아이템스 배열에서 찾아줌
            if(tier.items.find(item => item.id === id)) {
                return tier.id;
            }
        }
        return null;
    };

    //드래그가 끝낫을떄 실행되는 함수
    const haldlerDragEnd = (event) => {
        //active : 드래그한 아이템,over : 드롭한 대상
        const {active,over} = event;
        if(!over) return;

          if (over?.id === 'pool') {
        console.log("Pool에 드롭됨");
        // Pool에 드롭 시 로직 작성
    }

        const draggedId = active.id; //드래그한 챔피언 id
        const targetId = over.id; // 드롭 대상 id

        //원래 있던 티어의 id를 찾아준다 없으면 pool박스에 있던것
        const originTierid = findItemInTiers(draggedId);
        //이 챔피언이 pool박스에 잇는지 확인한다
        const isInPool = champData.find(item => item.id === draggedId);

        //pool 박스에서 -> 티어 박스로 이동
        if(isInPool && tiers.find(t => t.id === targetId)) {
            //드래그된 챔피언id의 객체를 champData 즉 기본 pool박스에의 목록에서 찾아서 변수에 저장해준다
            const movedItem = champData.find(item => item.id === draggedId);
            //찾은 챔피언을 제거해준다.
            setChampData(champData.filter(item => item.id !== draggedId));

            //챔피언을 드롭한 티어의 items배열에 추가해준다.
            setTiers(
                tiers.map(t =>
                    t.id === targetId ? {...t,items:[...t.items,movedItem]} : t
                )
            )
            return;
        }

                // 티어에서 pool로 이동
        if (originTierid && targetId === 'pool') {
            //원래 있던 티어박스에서 챔피언 객체를 찾아온다
            const movedItem = tiers.find(t => t.id === originTierid).items.find(i => i.id === draggedId);
            //원래 티어에서는 체거하고
            setTiers(tiers.map(t =>
            t.id === originTierid ? {...t, items: t.items.filter(i => i.id !== draggedId)} : t
            ));
            //pool champData에 객체를 추가해준다.
            setChampData([...champData, movedItem]);
            return;
        }


                    // 같은 티어 내 정렬
            if (originTierid && originTierid === targetId) {
                const tier = tiers.find(t => t.id === originTierid);
                //기존에 잇던 챔피언객체의 인덱스를 구해준다
                const oldIndex = tier.items.findIndex(i => i.id === draggedId);
                //새로 드랍될 위치를 구해준다
                const newIndex = tier.items.findIndex(i => i.id === over.id);


                if (oldIndex !== newIndex) {
                                //@dnd-kit/sortable이 제공하는 배열 순서 이동 함수를 이용하여 배열을 이동시켜주고
                const newItems = arrayMove(tier.items, oldIndex, newIndex);
                //해당 티어의 아이템의 상태를 업데이트 시켜준다
                setTiers(tiers.map(t =>
                    t.id === originTierid ? {...t, items: newItems} : t
                ));
                }
                return;
            }
        
            //서로 다른 티어간 이동                                                //targetId가 이동될 티어박스
        if(originTierid && originTierid !== targetId && tiers.find(t => t.id === targetId)) {
            //원래 티어에 잇던 챔피언 객체를 찾아서 저장해준다
            const moveItem = tiers.find(t => t.id === originTierid).items.find(i => i.id === draggedId);

            setTiers(
                tiers.map((t)=>{
                    //원래 티어에서는 삭제를 해주고
                    if (t.id === originTierid) {
                        return {...t,items : t.items.filter(i => i.id !== draggedId)};
                    }
                    //바뀐 티어에서는 추가를 해준다 
                    if (t.id === targetId) {
                        return {...t,items:[...t.items,moveItem]};
                    }
                    return t;
                })
            );
            return;
        }

    }



    
    useEffect(()=>{
        const data = async() => {
            try{
                const response = await axios.get("/riot/champion",{
                    params : {
                        tag : position
                    }
                });
                setChampData(response.data);
            }catch(error) {
                console.log(error)
            }
        }
        
        data();
    },[])


    useEffect(()=>{
        const data = async() => {
            try{
                const response = await axios.get("/riot/champion",{
                    params : {
                        tag : position
                    }
                });
                setChampData(response.data);
                setTiers(initialTiers);
            }catch(error) {
                console.log(error)
            }
        }
        
        data();
    },[position])

    const searchPosition = (position)=> {
        setPosition(position)
    }    


    return (
        <div>
            <DndContext sensors={sensors} collisionDetection={rectIntersection} onDragEnd={(event) => {haldlerDragEnd(event); setActiveId(null);}} onDragStart={(event) => setActiveId(event.active.id)}>    
                <div style={{display : "flex",flexDirection : "column",width:"88vw",paddingBottom : "2vh",justifyContent:"center",alignItems:"center"}}>
                    <div>
                        {tiers.map(tier => <Tier key={tier.id} tier={tier} />)}
                    </div>
                    <div style={{width:"70vw",marginBottom : "0.4vh",display : "flex",gap : "0.7vw",paddingTop:"3vh"}}>
                        <button style={{all:"unset",border : "1px solid #000",borderRadius : "3px",width : "9.3vw",height:"3vh",textAlign:"center",backgroundColor : position === "" ?  "#565666" : "#23272B",color:"#ffffffb7",cursor:"pointer" }}onClick={()=>{searchPosition("")}}>전체</button>
                        <button style={{all:"unset",border : "1px solid #000",borderRadius : "3px",width : "9.3vw",height:"3vh",textAlign:"center",backgroundColor : position === "Fighter" ?  "#565666" : "#23272B",color:"#ffffffb7",cursor:"pointer" }}onClick={()=>{searchPosition("Fighter")}}>전사</button>
                        <button style={{all:"unset",border : "1px solid #000",borderRadius : "3px",width : "9.3vw",height:"3vh",textAlign:"center",backgroundColor : position === "Tank" ?  "#565666" : "#23272B",color:"#ffffffb7",cursor:"pointer" }}onClick={()=>{searchPosition("Tank")}}>탱커</button>
                        <button style={{all:"unset",border : "1px solid #000",borderRadius : "3px",width : "9.3vw",height:"3vh",textAlign:"center",backgroundColor : position === "Mage" ?  "#565666" : "#23272B",color:"#ffffffb7",cursor:"pointer" }}onClick={()=>{searchPosition("Mage")}}>마법사</button>
                        <button style={{all:"unset",border : "1px solid #000",borderRadius : "3px",width : "9.3vw",height:"3vh",textAlign:"center",backgroundColor : position === "Assassin" ?  "#565666" : "#23272B",color:"#ffffffb7",cursor:"pointer" }}onClick={()=>{searchPosition("Assassin")}}>암살자</button>
                        <button style={{all:"unset",border : "1px solid #000",borderRadius : "3px",width : "9.3vw",height:"3vh",textAlign:"center",backgroundColor : position === "Marksman" ?  "#565666" : "#23272B",color:"#ffffffb7",cursor:"pointer" }}onClick={()=>{searchPosition("Marksman")}}>원딜</button>
                        <button style={{all:"unset",border : "1px solid #000",borderRadius : "3px",width : "9.3vw",height:"3vh",textAlign:"center",backgroundColor : position === "Support" ?  "#565666" : "#23272B",color:"#ffffffb7",cursor:"pointer" }}onClick={()=>{searchPosition("Support")}}>서포터</button>
                    </div>
                    <Pool champData={champData}></Pool>
                </div>  

                
                <DragOverlay>
                    {activeId ? (
                    <img
                        src={`https://ddragon.leagueoflegends.com/cdn/15.20.1/img/champion/${activeId}.png`}
                        alt={activeId}
                        style={{
                        width: 80,
                        height: 80,
                        cursor: 'grabbing',
                        pointerEvents: 'none', // 드래그 방해 방지
                        }}
                    />
                    ) : null}
                </DragOverlay>
            </DndContext>
        </div>
    )
}






function Pool({champData}){
    
    const { setNodeRef, isPoolOver } = useDroppable({ id: 'pool' });
    
    return (
        <div ref={setNodeRef} style={{  boxSizing: "border-box", border: isPoolOver ? '2px solid blue' : '1px solid gray', width: "70vw", minHeight: "20vh", padding: "2vh" }}>
            <SortableContext items={champData.map((i)=>(i.id))} strategy={verticalListSortingStrategy}>
                {champData.map((item,index)=>(
                    <SortableImgae key={item.id} id={item.id} url={`https://ddragon.leagueoflegends.com/cdn/15.20.1/img/champion/${item.id}.png`}></SortableImgae>
                ))}
            </SortableContext>
        </div>
    )
}





function Tier({tier}) {
     const { setNodeRef, isOver } = useDroppable({ id: tier.id });
     return (
        <div style={{display:"flex"}}>
            {/*티어 이름*/}
            <div style={{border : "1px solid #fff",width:"5vw",minHeight:"20vh",display:"flex",alignItems:"center",justifyContent:"center",backgroundColor:"#3d3b3bff"}}>
                <div style={{color:"#fff"}}>{tier.title}</div>
            </div>
            {/*챔피언 드롭 영역*/}
            <div key={tier.id} id={tier.id} ref={setNodeRef} style={{  border: isOver ? '2px solid blue' : '1px solid gray', width: "70vw", padding: 8,display : "flex",flexWrap : "wrap"}}>
                <div style={{display:"flex",gap:"0.3vw",flexWrap:"wrap"}}>
                    <SortableContext items={tier.items.map((i)=>(i.id))} strategy={verticalListSortingStrategy}>
                        {tier.items.map(item => (
                            <SortableImgae key={item.id} id={item.id} url={`https://ddragon.leagueoflegends.com/cdn/15.20.1/img/champion/${item.id}.png`}></SortableImgae>
                        ))}
                    </SortableContext>
                </div>
            </div>
        </div>
     )
}

//드래그 가능한 챔피언 이미지
function SortableImgae({id,url}) {                                                  //이미지를 드래그 할수 있게 해주는 훅 id값이 바로 드래그 대상(active) 드롭대상 (over) 
                                                                                    // id를 이용하여 각각의 SortableImgae컴포넌트가 화면상 이동이 되게 보여진다
    const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id });

    const style = {
        transform: CSS.Transform.toString(transform),//드래그할떄 이미지가 화면상 이동하게하는것처럼 보여짐 위치변화를 css에 맞게 변화시켜줌
        transition,
        opacity: isDragging ? 0.5 : 1,
        marginBottom: 8,
        cursor: 'grab',
        width: 80,
        height: 80,
    }
        return (
        <img
        ref={setNodeRef} //dnd-kit에게 실제로 추적해야할 dom요소를  알려주는 역활 react 는 직접 실제dom을 이용하지않으니 이렇게 ref를 통해 알려줌
        {...attributes} //드래그 동작을 위해 필요한 속성들을 넣어준다
        {...listeners} //마우스 이벤트를 등록 있어야 드래그가 작동한다
        src={url}
        alt={id}
        style={style}
        draggable={false} //dnd를 이용하기 위해 html 기본 드래글을 끈다
        />
    );
}
export default TierList;