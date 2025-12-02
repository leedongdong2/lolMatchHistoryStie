package com.reactpf.reactpf.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.reactpf.reactpf.dto.FindMemberBoardDto;

@Mapper
public interface FindMemberBoardMapper {
    
    List<FindMemberBoardDto> selectFindMeberBoard(Map<String,Object> paramMap);
    int countSelectFindMeberBoard(Map<String,Object> paramMap);
    List<FindMemberBoardDto> getRecentTenFindMemberBoard();
    FindMemberBoardDto selectFindMemberBoardDetail(int seq);
    int updateFindMemberBoardDetail(FindMemberBoardDto dto);
    int deleteFindMemberBoardDetail(int seq);
    int writeFindMemberBoardDetail(FindMemberBoardDto dto);
    List<FindMemberBoardDto> selectFindMemberBoardDetailComment(Map<String,Object> paramMap);
    int selectFindMemberBoardDetailCommentCount(int seq);
    int writeFindMemberBoardDetailComment(FindMemberBoardDto dto);
    int updateFindMemberBoardDetailComment(FindMemberBoardDto dto);
    int deleteFindMemberBoardDetailComment(int seq);
    void findMemberBoardIncreaseViews(int seq);
}
