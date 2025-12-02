//브라우져에서 html를 정화해주는 라이브러리
import DOMPurify from 'dompurify';

//허용된 태그와 속석을 남기고 나머지는 모두 제거해서 외부 스크립트 위험을 줄어들게 해준다
//태그가 브라우져에 출력되는거도 막아줌
function RiotTooltip({rawHtml}) {
    const sanitized = DOMPurify.sanitize(rawHtml,{
        ALLOWED_TAGS: ['b','i','strong','em','br','span','div','ul','li','p','img'],
        ALLOWED_ATTR: ['href','src','alt','class','title', 'style']
    });
                //검증 후 넣어도 됨을 react에게 알려준다
    return <div dangerouslySetInnerHTML={{ __html: sanitized }} />;
}

export default RiotTooltip;