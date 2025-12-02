package com.reactpf.reactpf.service;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reactpf.reactpf.dto.FindMemberBoardDto;
import com.reactpf.reactpf.mapper.FindMemberBoardMapper;

@Service
public class FindMemberBoardService {

    @Autowired
    FindMemberBoardMapper findMemberBoardMapper;

    //검색된 듀오매칭게시판 게시물들을 검색한다
    public List<FindMemberBoardDto> selectFindMeberBoard(Map<String,Object> paramMap){
        return findMemberBoardMapper.selectFindMeberBoard(paramMap);
    }
    // 게시물들의 숫자를 카운트한다
    public int countSelectFindMeberBoard(Map<String,Object> paramMap) {
        return findMemberBoardMapper.countSelectFindMeberBoard(paramMap);
    }
    //상위 10개의 게시물들을 가져온다
    public List<FindMemberBoardDto> getRecentTenFindMemberBoard(){
        return findMemberBoardMapper.getRecentTenFindMemberBoard();
    }
    //선택된 게시물의 상세페이지
    public FindMemberBoardDto selectFindMemberBoardDetail(int seq) {
        return findMemberBoardMapper.selectFindMemberBoardDetail(seq);
    }
    //게시물을 조회할떄마다 조회수를 1씩 증가
    public void findMemberBoardIncreaseViews(int seq) {
        findMemberBoardMapper.findMemberBoardIncreaseViews(seq);
    }

    //게시물에 prettytime 추가
    public List<FindMemberBoardDto> replacePrettyTime(List<FindMemberBoardDto> findMemberBoardDtos) {
        
        PrettyTime p = new PrettyTime();

        for(FindMemberBoardDto dto : findMemberBoardDtos){
            Date date = Date.from(dto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
            dto.setPrettyTime(p.format(date));
        }

        return findMemberBoardDtos;
    }

    //댓글에 prettytime 추가
    public FindMemberBoardDto replacePrettyTime(FindMemberBoardDto findMemberBoardDto) {


            PrettyTime p = new PrettyTime();
            Date date = Date.from(findMemberBoardDto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
            findMemberBoardDto.setPrettyTime(p.format(date));
        
        return findMemberBoardDto;
    }

    
    private List<FindMemberBoardDto> replaceCommentPrettyTime(List<FindMemberBoardDto> findMemberBoardDtos) {

        PrettyTime p = new PrettyTime();
        for(FindMemberBoardDto findMemberBoard : findMemberBoardDtos ){
            Date date = Date.from(findMemberBoard.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
            findMemberBoard.setPrettyTime(p.format(date));
        }

        return findMemberBoardDtos;
    }


    //게시물을 수정한다
    public String updateFindMemberBoardDetail(FindMemberBoardDto dto) {
        int result = findMemberBoardMapper.updateFindMemberBoardDetail(dto);
        if(result == 1) {
            return "수정되었습니다.";
        } else {
            return "수정 중 오류";
        }
    } 
    //선택된 게시물을 삭제한다
    public String deleteFindMemberBoardDetail(int seq) {
        int result = findMemberBoardMapper.deleteFindMemberBoardDetail(seq);

        if(result == 1) {
            return "삭제되엇습니다.";
        } else {
            return "삭제 중 오류";
        }
    }
    //선택된 게시물을 작성한다
    public String writeFindMemberBoardDetail(FindMemberBoardDto dto) {
        int result = findMemberBoardMapper.writeFindMemberBoardDetail(dto);

        if(result == 1) {
            return "작성되었습니다.";
        } else {
            return "작성 중 오류";
        }
    }

    /**
     * 
     * @param limit
     * @param offset
     * @param seq
     * @return
     * 선택된 게시물에 댓글을 가져온다
     */
    public List<FindMemberBoardDto> selectFindMemberBoardDetailComment(int limit,int offset,int seq) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("seq", seq);
        paramMap.put("limit",limit);
        paramMap.put("offset",offset);
        return findMemberBoardMapper.selectFindMemberBoardDetailComment(paramMap);
    }
    //선택된 게시물의 댓글 수 
    public int selectFindMemberBoardDetailCommentCount(int seq) {
        return findMemberBoardMapper.selectFindMemberBoardDetailCommentCount(seq);
    }
    //선택된 게시물에 댓글을 작성
    public String writeFindMemberBoardDetailComment(FindMemberBoardDto dto) {
        int result = findMemberBoardMapper.writeFindMemberBoardDetailComment(dto);
        
        if(result == 1){
            return "작성되었습니다";
        } else {
            return "작성중 오류";
        }
    }
    //선택된 게시물에 댓글을 수정
    public String updateFindMemberBoardDetailComment(FindMemberBoardDto dto) {
        System.out.println(dto.getContent() + dto.getSeq());
        
        int result = findMemberBoardMapper.updateFindMemberBoardDetailComment(dto);
        if(result == 1){
            return "수정되었습니다";
        } else {
            return "수정중 오류";
        }
    }
    
    //선택된 게시물에 댓글을 삭제
    public String deleteFindMemberBoardDetailComment(int seq) {
        int result = findMemberBoardMapper.deleteFindMemberBoardDetailComment(seq);

        if(result == 1) {
            return "삭제되었습니다";
        } else {
            return "삭제중 오류";
         }
    }  


}
