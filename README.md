<table>
  <tr>
    <td
      <p></p>
      <p>롤 전적검색 사이트</p>
      <p>---------------------<p>
      사용언어  :  html,css,javascript,java <br> 
      프론트앤드 : React <br>
      백앤드 : Spring Boot (주요 라이브러리 : mybatis,spring security) 빌드도구 : gradle<br> 
      래퍼런스 사이트 : DataDragon,riot API - RiotDeveloperPortal(API 자료 제공),OP.GG(외형 참고 사이트),ChatGPT(전반적인 프로젝트 도움)      
      <p>평소 좋아하는 게임인 롤의 전적검색 사이트를 만들어 보았습니다.
      <br>즐겨 이용하는 op.gg 사이트를 참고 하여 이용자의 아이디를 검색하거나 자유로운 커뮤니티 이용 챔피언들의 티어나눠보기를 할수있는 사이트입니다.
    </td>
  </tr>
  <tr>
    <td>
      <img width="1200" alt="메인화면" src="https://github.com/user-attachments/assets/e273006a-14b8-4e1a-ae06-24e00a6e48cf" />
    </td>
    <td>
      <b>메인화면</b><br><br>
      상층의 메뉴바와 전적 검색창이 있고 <br>로테이션 챔피언을 받아와서 표시됩니다 <br>로그인 화면으로 갈수 있는 마이페이지 버튼이 있습니다
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="272" height="597" alt="로그인 후 화면" src="https://github.com/user-attachments/assets/dff7f087-8902-414d-90da-69b0efd4a813" />  
    </td>
    <td>
      <b>로그인 후 화면</b><br><br>
      마이페이지 버튼으로 들어가 <br> 로그인페이지에서 
      로그인을 하게 되면 
      우측 마이페이지 버튼이 회원가입시 <br>입력된 유저의 정보로 
      롤id,전적,티어등을 받아와 표시됩니다
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="883" height="709" alt="로그인페이지" src="https://github.com/user-attachments/assets/39378581-220f-4d4e-9dac-074fe1854436" />
    </td>
    <td>
      <b>로그인 페이지</b><br><br>
      회원가입시 정보로 로그인을 하는 페이지 <br>
      홈페이지 회원 정보와 구글로그인정보 중 하나로 로그인이 가능합니다
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="647" height="379" alt="회원가입페이지" src="https://github.com/user-attachments/assets/36adb931-3cb6-4aca-9d30-1a578429f1ec" />
    </td>
    <td>
      <b>회원가입 페이지</b><br><br>
      회원가입을 하는 페이지<br>
      로그인 할 아이디 비밀번호 
      닉네임 롤 아이디를 적고 중복검사를 한 후 회원가입 
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="690" height="382" alt="구글아이디로 회원가입시" src="https://github.com/user-attachments/assets/b75996ca-f108-4145-8e19-2096b30e3ac8" />
    </td>
    <td>
      <b>회원가입 페이지(구글)</b><br><br>
      회원가입을 하는 페이지<br>
      구글 아이디로 로그인을 하려 했을때 홈페이지에 가입된
      구글 아이디가 없다면 회원가입창으로 보내집니다.
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="1899" height="938" alt="전적 검색 화면" src="https://github.com/user-attachments/assets/b1b3404b-db27-45be-8c9d-5c0e467deaef" />
    </td>
    <td>
      <b>전적검색 페이지</b><br><br>
      전적검색을 한 후 나타는 페이지<br>
      검색한 아이디를 토대로 유저의 티어 및 승패 숙련도 상위 4개의 챔피언 
      유저 전적이 최신순으로 나옵니다
      랭크나 칼바람 골라서도 조회가능합니다
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="1026" height="763" alt="더보기 시" src="https://github.com/user-attachments/assets/9075d371-3306-4828-8d8a-ac7fc9b72713" />
    </td>
    <td>
      <b>전적검색 페이지(더보기)</b><br><br>
      맨 하단에 있는 더보기 버튼을 누를시 <br> 5개씩 유저의 전적이 계속 조회됩니다
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="962" height="131" alt="더보기 시 변화" src="https://github.com/user-attachments/assets/b2d1e020-3732-4d25-b48c-321637929f29" />
    </td>
    <td>
      <b>전적검색 페이지(더보기)</b><br><br>
      더보기를 누를때마다 조회된 전적의 총 갯수와 유저가 플레이한 라인의 그래프가 변화합니다(칼바람 제외)
    </td>
  </tr> 
  <tr>
    <td align="center">
      <img width="970" height="863" alt="전적 상세보기" src="https://github.com/user-attachments/assets/ac4f3aa4-3f35-42a9-aea9-dc1013ed8083" />
    </td>
    <td>
      <b>전적 상세보기 페이지</b><br><br>
      조회된 게임 전적의 상세 내용을 볼 수 있습니다  <br>
      검색한 유저와 같이 플레이한 유저들의 게임 정보(아이템,룬 등)와 
      팀 별 오브젝트,골드 수급,킬 등이 표시됩니다 
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="965" height="695" alt="전적 상세보기 빌드" src="https://github.com/user-attachments/assets/b3583b46-a029-4a6c-8060-9ee624a7451d" />
    </td>
    <td>
      <b>전적 상세보기 페이지(빌드)</b><br><br>
      조회된 게임 전적의 상세 내용을 볼 수 있습니다  <br>
      검색한 유저의 아이템 빌드,스킬 트리,
      룬 빌드를 알수 있습니다
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="968" height="736" alt="전적 상세보기 수급 챠트" src="https://github.com/user-attachments/assets/7d8aea8a-3cbd-41d0-9a42-045e809da149" />
    </td>
    <td>
      <b>전적 상세보기 페이지(챠트)</b><br><br>
      조회된 게임 전적의 상세 내용을 볼 수 있습니다  <br>
      검색한 유저의 cs 수급,골드 수급을 알수 있습니다.(4분 단위)
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="1920" height="956" alt="자유게시판" src="https://github.com/user-attachments/assets/3d9d6c13-19fc-440c-8231-d4316668edea" />
    </td>
    <td>
      <b>자유 게시판</b><br><br>
      유저들이 등록한 게시글을 볼 수 있습니다  <br>
      좌측에는 최신 등록된 게시글 10개를 볼 수 있습니다.
      정렬을 날짜와 조회수 로 할 수 있습니다.
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="1217" height="842" alt="자유게시판 디테일" src="https://github.com/user-attachments/assets/76fb98c3-d4ca-486a-858d-061fa24395f6" />
    </td>
    <td>
      <b>자유 게시판(상세)</b><br><br>
      유저들이 등록한 게시글의 상세페이지를 볼 수 있습니다 <br>
      좌측에는 최신 등록된 게시글 10개를 볼 수 있습니다.
      게시글의 내용과 작성자,작성 시간,조회 수,댓글 수를 볼 수 있습니다.
      댓글을 등록 할 수 있습니다
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="843" height="858" alt="자유게시판 댓글과 대댓글" src="https://github.com/user-attachments/assets/739ceb12-6305-4e7d-b688-6a4f94b93f8d" />
    </td>
    <td>
      <b>자유 게시판(댓글)</b><br><br>
      상세페이지의 댓글 중 대댓글을 등록하거나 수정할 수 있습니다.
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="811" height="715" alt="자유게시판 글쓰기 (수정 동일)" src="https://github.com/user-attachments/assets/543ae989-5a3c-4056-9849-bbb986596d1b" />
    </td>
    <td>
      <b>자유 게시판(글쓰기)</b><br><br>
      자유 게시판에 게시물을 등록 할 수 있습니다.
      Toast UI Editor 사용하여 이미지나 글을 작성 가능합니다.
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="919" height="890" alt="자유게시판 수정" src="https://github.com/user-attachments/assets/2e245364-66bf-4cd0-8dcb-917da23a8514" />
    </td>
    <td>
      <b>자유 게시판(수정)</b><br><br>
      자유 게시판에 등록 한 자신의 게시물을 수정 할 수 있습니다
      Toast UI Editor 사용하여 이미지나 글을 수정 가능합니다.
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="1915" height="946" alt="듀오매칭 게시판" src="https://github.com/user-attachments/assets/de422fd9-be9c-4ef8-8d09-4f9d30aeebc8" />
      <img width="848" height="787" alt="듀오매칭게시판 상세와 댓글" src="https://github.com/user-attachments/assets/77307ab1-0e7c-4fe8-b88a-9399c8ae29e2" />
      <img width="841" height="883" alt="듀오매칭게시판 수정" src="https://github.com/user-attachments/assets/28181977-8048-4491-8f21-c9cea2ce8073" />
    </td>
    <td>
      <b>듀오찾기 게시판</b><br><br>
        듀오를 원하는 유저를 찾는 게시판입니다.
        게시글을 남기고 댓글을 등록 할 수 있습니다.
        자신의 게시물을 수정 할 수 있습니다.
        이미지는 등록 할 수 없습니다.
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="1495" height="817" alt="티어박스" src="https://github.com/user-attachments/assets/bb86128d-e777-4a15-8b86-f842a63ea37c" />
      <img width="1530" height="886" alt="티어박스2" src="https://github.com/user-attachments/assets/073d45f2-a197-4ff0-b772-e9f4716e1dce" />
    </td>
    <td>
      <b>티어 박스</b><br><br>
        유저들이 롤의 챔피언들을 자신의 생각대로 티어를 나누어 볼 수 있는 페이지입니다.<br>
        아래의 박스에서 챔피언의 이미지를 드래그해 티어 박스로 나눌 수 있습니다.<br>
        각 챔피언들의 역활군에 따라서 검색 가능합니다.
    </td>
  </tr>
</table>








