package com.reactpf.reactpf.controller;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reactpf.reactpf.dto.FindMemberBoardDto;
import com.reactpf.reactpf.dto.PageDto;
import com.reactpf.reactpf.service.FindMemberBoardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class FindMemberBoardController {
    
    private final FindMemberBoardService findMemberBoardService;
    
    /**
     ** 
     * @param page
     * @param paramMap
     * @return
     * 듀오매칭게시판의
     * 검색한 게시물을 가져온다
     * 
     */
    @GetMapping("/findMember")
    public ResponseEntity<Map<String,Object>> getFindMemberBoard(@RequestParam(name = "page", defaultValue = "1")int page,@RequestParam(required = false) Map<String,Object> paramMap) {
        


        int pageSize = 3;
        int offset = (page-1) * pageSize;
        paramMap.put("pageSize", pageSize);
        paramMap.put("offset", offset);        

        
        List<FindMemberBoardDto> findMemberBoardList = findMemberBoardService.selectFindMeberBoard(paramMap);
        int totalCount = findMemberBoardService.countSelectFindMeberBoard(paramMap);
        PageDto pageDto = new PageDto(page, pageSize, totalCount);
        
        findMemberBoardService.replacePrettyTime(findMemberBoardList);

        List<FindMemberBoardDto> recentTenFindMemberBoard = findMemberBoardService.getRecentTenFindMemberBoard();

        Map<String,Object> data = new HashMap<>();

        data.put("recentTenFindMemberBoard", recentTenFindMemberBoard);
        data.put("page", pageDto);
        data.put("searchText", paramMap.get("searchText"));
        data.put("searchType", paramMap.get("searchType"));
        data.put("matchType", paramMap.get("matchType"));
        data.put("findMemberBoardList", findMemberBoardList);
        return ResponseEntity.ok(data);
    }

    
    /**
     * 
     * @param seq
     * @param page
     * @return
     * 게시물의 상세정보를 가져온다
     */
    @GetMapping("/findMemberBoard/detail")
    public ResponseEntity<Map<String,Object>> getfindMemberBoardDetail(@RequestParam("seq") int seq) {
       
       

        findMemberBoardService.findMemberBoardIncreaseViews(seq);

        FindMemberBoardDto findMemberBoardDto = findMemberBoardService.selectFindMemberBoardDetail(seq);

        PrettyTime p = new PrettyTime();
        Date date = Date.from(findMemberBoardDto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
        findMemberBoardDto.setPrettyTime(p.format(date));

        List<FindMemberBoardDto> recentTenFindMemberBoard = findMemberBoardService.getRecentTenFindMemberBoard(); 
        
        Map<String,Object> data = new HashMap<>();

        data.put("recentTenFindMemberBoard", recentTenFindMemberBoard);
        data.put("findMemberBoardDto", findMemberBoardDto);

        return ResponseEntity.ok(data);
    }

    @GetMapping("/findMemberBoard/detail/comment")
    public ResponseEntity<Map<String,Object>> getMethodName(@RequestParam("seq") int seq,@RequestParam(name = "page", defaultValue = "1" )int page) {
        
        int limit = 5;
        int offset = (page-1)*limit;

        List<FindMemberBoardDto> findMemberBoardDetailComment = findMemberBoardService.selectFindMemberBoardDetailComment(limit, offset, seq);
        findMemberBoardService.replacePrettyTime(findMemberBoardDetailComment);
        int totalCount = findMemberBoardService.selectFindMemberBoardDetailCommentCount(seq);
        PageDto pageDto = new PageDto(page, limit, totalCount);

        Map<String,Object> data = new HashMap<>();
        
        data.put("findMemberBoardDetailComment",findMemberBoardDetailComment);
        data.put("page",pageDto);

        return ResponseEntity.ok(data);
    }
    












    /**
     * 
     * @param seq
     * @return
     * 수정할 게시물의 정보를 가져온다
     */
    @GetMapping("/findMemberBoard/page/update")
    public ResponseEntity<FindMemberBoardDto> getFindMemberBoardUpdatePage(@RequestParam("seq") int seq) {
        FindMemberBoardDto findMemberBoardDto = findMemberBoardService.selectFindMemberBoardDetail(seq); 
        return ResponseEntity.ok(findMemberBoardDto);
    }

    /**
     * 
     * @param dto
     * @param authentication
     * @return
     * 게시물을 작성
     */
    @PostMapping("/findMemberBoard/write")
    public ResponseEntity<?> writeFindMemberBoardDetail(@RequestBody FindMemberBoardDto dto) {
        String result = findMemberBoardService.writeFindMemberBoardDetail(dto);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 
     * @param seq
     * @param dto
     * @return
     * 게시물을 수정한다
     */
    @PutMapping("/findMemberBoard/update/{seq}")
    public ResponseEntity<?> updateFindMemberBoardDetail(@PathVariable("seq") Long seq, @RequestBody FindMemberBoardDto dto) {
        dto.setSeq(seq);
        String result = findMemberBoardService.updateFindMemberBoardDetail(dto);
        return ResponseEntity.ok(result);
    }
    /**
     * 
     * @param seq
     * @return
     * 게시물을 삭제한다
     */
    @DeleteMapping("/findMemberBoard/delete/{seq}")
    public ResponseEntity<?> deleteFindMemberBoardDetail(@PathVariable("seq") int seq) {
        String result = findMemberBoardService.deleteFindMemberBoardDetail(seq);
        return ResponseEntity.ok(result);
    }

    /**
     * 
     * @param dto
     * @param authentication
     * @return
     * 선택된 게시물에 댓글을 작성한다
     */
    @PostMapping("/findMemberBoard/comment/write")
    public ResponseEntity<?> postMethodName(@RequestBody FindMemberBoardDto dto,Authentication authentication) {
        
        String result = findMemberBoardService.writeFindMemberBoardDetailComment(dto); 
        return ResponseEntity.ok(result);
    }
    /**
     * 
     * @param seq
     * @param dto
     * @return
     * 선택된 게시물에 댓글을 수정 
     */
    @PutMapping("/findMemberBoard/comment/update/{commentSeq}")
    public ResponseEntity<?> putMethodName(@PathVariable("commentSeq") Long seq, @RequestBody FindMemberBoardDto dto) {
        dto.setSeq(seq);
        String result = findMemberBoardService.updateFindMemberBoardDetailComment(dto);
        return ResponseEntity.ok(result);
    }
    /**
     * 
     * @param seq
     * @return
     * 선택된 게시물에 댓글을 삭제
     */
    @DeleteMapping("/findMemberBoard/commnet/delete/{commentSeq}")
    public ResponseEntity<?> deleteFindMemberBoardDetailComment(@PathVariable("commentSeq") int seq) {
        String result = findMemberBoardService.deleteFindMemberBoardDetailComment(seq);
        return ResponseEntity.ok(result);
    }
    
}
