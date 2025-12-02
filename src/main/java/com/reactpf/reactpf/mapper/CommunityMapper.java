package com.reactpf.reactpf.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reactpf.reactpf.dto.CommunityCommentDto;
import com.reactpf.reactpf.dto.CommunityDto;

@Mapper
public interface CommunityMapper {
    
    int communityWrite(CommunityDto dto);
    List<CommunityDto> searchCommunity(CommunityDto dto);
    int countCommunity(CommunityDto dto);
    List<CommunityDto> getRecentTenCommunity();
    CommunityDto getCommunityDetail(int seq);
    int communityUpdate(CommunityDto dto);
    int communityDelete(int seq);
    List<CommunityCommentDto> getCommunityTopComment(HashMap<String,Object> pageParams);
    int communityCommentWrite(CommunityCommentDto dto);
    List<CommunityCommentDto> getChildComment(int seq);
    int countCommuntyComment(int seq);
    int updateCommunityComment(CommunityCommentDto dto);
    int deleteCommunityComment(Long seq);
    void communityCommentIncreaseViews(int seq);
    
    
}
    

