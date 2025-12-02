package com.reactpf.reactpf.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.reactpf.reactpf.dto.CommunityCommentDto;
import com.reactpf.reactpf.dto.CommunityDto;
import com.reactpf.reactpf.dto.PageDto;
import com.reactpf.reactpf.service.CommunityService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;




@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class CommunityController {
    
    private final CommunityService communityService;

    /**
     * 
     * @param dto
     * @param page
     * @param searchType
     * @param searchText
     * @return
     * 커뮤니티 게시판으로 이동한다
     */
    @GetMapping("/community")
    public ResponseEntity<Map<String,Object>> moveCommunity(CommunityDto dto,@RequestParam(name = "page",defaultValue = "1") int page,@RequestParam(value="searchType",required = false) String searchType,@RequestParam(name="searchText",required = false) String searchText,@RequestParam(name="searchOrder",defaultValue = "Latest")String searchOrder) {
        
        if("all".equals(searchType)) {
            dto.setContent(searchText);
            dto.setTitle(searchText);
            dto.setNickname(searchText);
        } else if("title".equals(searchType)){
            dto.setTitle(searchText);
        } else if("author".equals(searchType)){
            dto.setNickname(searchText);
        } else if("content".equals(searchType)){
            dto.setContent(searchText);
        }
        dto.setSearchOrder(searchOrder);
        
        int pageSize = 3;
        int offset = (page - 1) * pageSize;

        dto.setLimit(pageSize);
        dto.setOffset(offset);

        
        List<CommunityDto> dtos = communityService.serchCommunity(dto);
        int totalCount = communityService.countCommunity(dto);
        PageDto pageDto = new PageDto(page, pageSize, totalCount);

        communityService.replacePrettyTime(dtos);

        List<CommunityDto> recentTenCommunity  = communityService.getRecentTenCommunity();

        Map<String,Object> data = new HashMap<>();
        
        data.put("recentTenCommunity", recentTenCommunity);
        data.put("communityListDto", dtos);
        data.put("page", pageDto);
        
        return ResponseEntity.ok(data);
    }


    
    /**
     * 
     * @param communityText
     * @param authentication
     * @return
     * @throws IOException
     *  게시물을 작성한다
     */
    @PostMapping("/community/write")
    public ResponseEntity<?> writeCommunity(@RequestBody CommunityDto communityDto) throws IOException {
        

        System.out.println(communityDto.getNickname());

        String content = communityService.communityWriteImgUpload(communityDto.getContent());

        communityDto.setContent(content);

        String result = communityService.commuityWrite(communityDto);

        return ResponseEntity.ok(result);
    }

    /**
     * 
     * @param communitySeq
     * @return
     * 수정할 게시물의 정보를 가져온다
     */
    @GetMapping("/community/updateDetail")
    public ResponseEntity<CommunityDto> getMethodName(@RequestParam("communitySeq") int communitySeq) {
        CommunityDto dto = communityService.getCommunityDetail(communitySeq); 
        return ResponseEntity.ok(dto);
    }

    /**
     * 
     * @param communitySeq
     * @param updateDto
     * @return
     * @throws IOException
     * 
     * 선택된 게시물을 수정해준다
     */
    @PutMapping("/community/updateCc/{communitySeq}")
    public ResponseEntity<?> postMethodName(@PathVariable("communitySeq") int communitySeq, @RequestBody CommunityDto updateDto) throws IOException {
        
        CommunityDto originalDto = communityService.getCommunityDetail(communitySeq);
        String updateContent = communityService.communityUpdateImgUpload(originalDto.getContent(), updateDto.getContent());
        updateDto.setContent(updateContent);
        updateDto.setSeq(communitySeq);
        String result = communityService.communityUpdate(updateDto);
        return ResponseEntity.ok(result);
    }
    /**
     * 
     * @param communitySeq
     * @return
     * 선택된 게시물을 삭제한다
     */
    @DeleteMapping("/community/deleteCc/{communitySeq}")
    public ResponseEntity<?> deleteCommunityContent(@PathVariable("communitySeq") int communitySeq){
        CommunityDto orginalDto = communityService.getCommunityDetail(communitySeq);
        communityService.deleteImg(orginalDto.getContent());
        String result = communityService.commnuityDelete(communitySeq);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException
     * 스마트 에디터2 에서 이미지를 임시 업로드하기위한 메소드
     */
    @PostMapping("/upload/image")
    public ResponseEntity<String> postUploadImage(@RequestParam("file") MultipartFile file) throws IOException{
       
        String imagePath = communityService.tempImgUpload(file);
        
        return ResponseEntity.ok(imagePath);
    }

    /**
     * 
     * @param communitySeq
     * @param page
     * @return
     * 게시물의 상세정보를 가져온다
     */
    @GetMapping("/community/detail")
    public ResponseEntity<Map<String,Object>> getCommunityDetail(@RequestParam("communitySeq") int communitySeq) {
        
        communityService.communityCommentIncreaseViews(communitySeq);
        
        CommunityDto dto = communityService.getCommunityDetail(communitySeq);

        communityService.replacePrettyTime(dto);


        List<CommunityDto> recentTenCommunity = communityService.getRecentTenCommunity();

        Map<String,Object> data = new HashMap<>();

        data.put("recentTenCommunity", recentTenCommunity);
        data.put("communityDetailDto", dto);

        return ResponseEntity.ok(data);
    }



    
    /**
     * 
     * @param communitySeq
     * @param page
     * @return
     * 게시물의 상세페이지의 댓글을 가져온다      */
    @GetMapping("/community/detail/comment")
    public ResponseEntity<Map<String,Object>> getCommunityDetailComment(@RequestParam("communitySeq") int communitySeq,@RequestParam(name = "page", defaultValue = "1") int page) {
        
        
        int limit = 5;
        int offset = (page-1)* limit;
        
        List<CommunityCommentDto> comments = communityService.buildCommentTree(null,page,communitySeq,limit,offset);

        int totalCount = communityService.countCommuntyComment(communitySeq);

        communityService.replaceCommentPrettyTime(comments);

        PageDto pageDto = new PageDto(page, limit, totalCount);
        
        Map<String,Object> data = new HashMap<>();
        
        data.put("communityCommentList", comments);
        data.put("currentPage", pageDto);

        return ResponseEntity.ok(data);
    }
    /**
     * 
     * @param dto
     * @param authentication
     * @return
     * 해당 게시물에 댓글을 작성하는 메소드
     */
    @PostMapping("/community/comment/write")
    public ResponseEntity<?> communityCommentWrite(@RequestBody CommunityCommentDto dto) {
        String result = communityService.communityCommentWrite(dto);
        return ResponseEntity.ok(result);
    }
    /**
     * 
     * @param seq
     * @param dto
     * @return
     * 해당 게시물에 댓글을 수정
     */
    @PutMapping("/community/comment/update/{seq}")
    public ResponseEntity<?> putMethodName(@PathVariable Long seq, @RequestBody CommunityCommentDto dto) {
        dto.setSeq(seq);
        String result = communityService.updateCommunityComment(dto);
        return ResponseEntity.ok(result);
    }
    /**
     * 
     * @param seq
     * @return
     * 해당 게시물에 댓글을 삭제
     */
    @DeleteMapping("/community/comment/delete/{seq}")
    public ResponseEntity<?> deleteCommunityComment(@PathVariable Long seq) {
        String result = communityService.deleteCommunityComment(seq);
        return ResponseEntity.ok(result);
    }
    
}
