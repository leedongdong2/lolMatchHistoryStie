package com.reactpf.reactpf.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.reactpf.reactpf.dto.CommunityCommentDto;
import com.reactpf.reactpf.dto.CommunityDto;
import com.reactpf.reactpf.mapper.CommunityMapper;

@Service
public class CommunityService {
    
    @Autowired
    CommunityMapper communityMapper;

    //검색된 커뮤니티 글 목록을 반환
    public List<CommunityDto> serchCommunity(CommunityDto communityDto){
        return communityMapper.searchCommunity(communityDto);
    }
    
    //검색된 커뮤니티 글 목록의 갯수
    public int countCommunity(CommunityDto communityDto) {
        return communityMapper.countCommunity(communityDto);    
    }
    
    //최근 상위 10개의 커뮤니티 글 목록
    public List<CommunityDto> getRecentTenCommunity(){
        return communityMapper.getRecentTenCommunity();
    }

    //선택된 커뮤니티 글의 상세
    public CommunityDto getCommunityDetail(int seq){
        return communityMapper.getCommunityDetail(seq);
    }

    //선택된 커뮤니티 글의 댓글 갯수
    public int countCommuntyComment(int seq) {
        return communityMapper.countCommuntyComment(seq);
    }

    
    //게시물에 prettytime 추가
    public List<CommunityDto> replacePrettyTime(List<CommunityDto> communityDtos) {

        for(CommunityDto communityDto : communityDtos) {
            PrettyTime p = new PrettyTime();
            Date date = Date.from(communityDto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
            communityDto.setPrettyTime(p.format(date));
        }
        return communityDtos;
    }


    public CommunityDto replacePrettyTime(CommunityDto communityDto) {

            PrettyTime p = new PrettyTime();
            Date date = Date.from(communityDto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
            communityDto.setPrettyTime(p.format(date));
            
        return communityDto;
    }

    //댓글에 prettytime 추가
    public List<CommunityCommentDto> replaceCommentPrettyTime(List<CommunityCommentDto> commentDtos) {
            PrettyTime p = new PrettyTime();
            for(CommunityCommentDto commentDto : commentDtos) {
             replaceCommentPrettyTimeRecursive(commentDto,p);
            }

        return commentDtos;
    }


    private void replaceCommentPrettyTimeRecursive(CommunityCommentDto commentDto, PrettyTime p) {
        Date date = Date.from(commentDto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
        commentDto.setPrettyTime(p.format(date));

        if (commentDto.getChildren() != null) {
            for (CommunityCommentDto child : commentDto.getChildren()) {
                replaceCommentPrettyTimeRecursive(child, p); // 재귀 호출
            }
        }
    }


    


    /**
     * 
     * @param comments
     * @param page
     * @param communitySeq
     * @param limit
     * @param offset
     * @return List<CommunityCommentDto>
     * 댓글 과 대댓글을 가져온다 
     */
    public List<CommunityCommentDto> buildCommentTree(List<CommunityCommentDto> comments,int page,int communitySeq,int limit,int offset) {

        HashMap<String,Object> pageParams = new HashMap<>();
        pageParams.put("limit", limit);
        pageParams.put("offset",offset);
        pageParams.put("postSeq",communitySeq);
        
        //최상위 댓글(부모가 없다)을 가져온다
        List<CommunityCommentDto> topCommentDtos = communityMapper.getCommunityTopComment(pageParams);
        
        
        List<Long> parentSeq = new ArrayList<>();
        //최상위 댓글들의 seq를 따로 list에 저장해준다
        for(CommunityCommentDto comment : topCommentDtos){
            parentSeq.add(comment.getSeq());
        }

        //해당 게시글의 모든 자식 댓글들을 가져온다
        List<CommunityCommentDto> childComments = communityMapper.getChildComment(communitySeq);

        Map<Long,CommunityCommentDto> commentMap = new HashMap<>();
        
        //최상위 댓글을 맵에 추가하면서 depth 0으로 지정해준다
        for(CommunityCommentDto top : topCommentDtos){
            top.setDepth(0);
            commentMap.put(top.getSeq(), top);
        }
        //자식 댓글도 맵에 추가해준다
        for(CommunityCommentDto child : childComments) {
            commentMap.put(child.getSeq(),child);
        }
        //자식 댓글들을 각각 부모 댓글에 연결해준다
        for(CommunityCommentDto comment : childComments) {
            CommunityCommentDto parent = commentMap.get(comment.getParentSeq());
            if(parent != null) {
                parent.getChildren().add(comment);
            }
        }
        //모든 상위 댓글부터 재귀적으로 depth 설정해준다
        for (CommunityCommentDto comment : topCommentDtos) {
            setDepthRecursive(comment, 0);
        }

        //최종적으로 트리구조가된 최상위 댓글을 반환
        return topCommentDtos;
    }

    //depth를 설정하는 재귀함수
    private void setDepthRecursive(CommunityCommentDto parent, int depth) {
        parent.setDepth(depth);
        for (CommunityCommentDto child : parent.getChildren()) {
            setDepthRecursive(child, depth + 1);
        }
    }


    //커뮤니티 글 삭제
    public String commnuityDelete(int seq){
        int result = communityMapper.communityDelete(seq);
        
        if (result == 1) {
            return "삭제되었습니다";
        } else {
            return "삭제 중 오류";
        }
        
    }
    
    //커뮤니티 글 수정
    @Transactional
    public String communityUpdate(CommunityDto communityDto){
        int result = communityMapper.communityUpdate(communityDto);
        
        if (result == 1) {
            return "작성되었습니다";
        } else {
            return "작성 중 오류";
        }
        
    }

    //커뮤니티 글 작성
    @Transactional
    public String commuityWrite(CommunityDto communityDto){
        
        int result = communityMapper.communityWrite(communityDto);
        
        if (result == 1) {
            return "작성되었습니다";
        } else {
            return "작성 중 오류";
        }
        
    }

    //해당 커뮤니티 글에 대한 댓글작성
    public String communityCommentWrite(CommunityCommentDto dto){
        
        int result = communityMapper.communityCommentWrite(dto);
        
        if (result == 1){
            return "작성되었습니다";
        } else {
            return "작성 중 오류";
        }
    }
    //해당 커뮤니티 글에 대한 댓글수정
    public String updateCommunityComment(CommunityCommentDto dto) {
        int result = communityMapper.updateCommunityComment(dto);

        if (result == 1) {
            return "수정되었습니다";
        } else {
            return "수정 중 오류";
        }
    }

    //해당 커뮤니티 글에 대한 댓글삭제
    public String deleteCommunityComment(Long seq) {
        int result = communityMapper.deleteCommunityComment(seq);

        if (result == 1) {
            return "삭제되었습니다";
        } else {
            return "삭제 중 오류";
        }
    }
    
    //커뮤니티가 조회될떄 마다  조회수를 1씩 증가시켜준다
    public void communityCommentIncreaseViews(int seq) {
        communityMapper.communityCommentIncreaseViews(seq);
    }
    
    // 커뮤니티 글 작성시 이미지 업로드 와 본문 반환
    public String communityWriteImgUpload(String content) throws IOException {
        
        Pattern pattern = Pattern.compile("!\\[.*?\\]\\((.*?)\\)");
        Matcher matcher = pattern.matcher(content);
           
        
                while (matcher.find()) {
                    // 마크다운에서 이미지문구를 필터링해서 이미지경로만 뽑아옴
                    String imageUrl = matcher.group(1);
                    // 파일명만 추출
                    String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                    //임시 경로의 이미지 파일 객체 생성
                    File tempFile = new File( "C:\\Users\\must1\\Desktop\\temp\\" + fileName);
                    //실제 저장될 위치의 이미지 파일 객체 생성
                    File newFile = new File("C:\\Users\\must1\\Desktop\\communityImg\\" + fileName);
                    //파일을 임시 폴더에서 실제 폴더로 이동 (같은 이름 있으면 덮어쓰기)
                    Files.move(tempFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    // HTML 내 <img> 태그의 src 경로를 실제 접근 가능한 URL로 변경
                    content = content.replace(imageUrl, "/imagePath/posts/" + fileName);

                }


        
        return content;
    }

    // 게시물 수정시 이미지 업로드 와 본문 반환
    public String communityUpdateImgUpload(String originalContent,String updateContent) throws IOException  {
            
            String tempUploadDir = "C:\\Users\\must1\\Desktop\\temp\\";
            String newUploadDir = "C:\\Users\\must1\\Desktop\\communityImg\\";

            Pattern pattern = Pattern.compile("!\\[.*?\\]\\((.*?)\\)");
            Matcher originalMatcher = pattern.matcher(originalContent);
            Matcher updateMatcher =  pattern.matcher(updateContent);
            
            //기존 이미지가 없을떄
            if(!originalMatcher.find()){
                // 수정 이미지도 없다면 그대로 본문을 반환해준다
                if(!updateMatcher.find()){
                    return originalContent;
                } else {//수정 이미지만 있다면 새 이미지를 업로드 해준다
                    while(updateMatcher.find()) {
                            
                        String imageUrl = updateMatcher.group(1);
                        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                        File tempFile = new File( "C:\\Users\\must1\\Desktop\\temp\\" + fileName);
                        File newFile = new File("C:\\Users\\must1\\Desktop\\communityImg\\" + fileName);
                        Files.move(tempFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        updateContent = updateContent.replace(imageUrl, "/imagePath/posts/" + fileName);
                    }
                    return updateContent;
                }
            } else { //기존 이미지가 있을 시
                //기존 이미지만 있고 수정본엔 이미지가 없을시 기존 이미지 삭제
                if(!updateMatcher.find()) {

                    while(originalMatcher.find()) {

                        String imageUrl = originalMatcher.group(1);
                        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

                        File tempOriginalFile = new File( tempUploadDir + fileName);
                        File originalFile = new File(newUploadDir + fileName);

                        tempOriginalFile.delete();
                        originalFile.delete();
                    }
                    return updateContent;
                } else {    
                     //기존에도 이미지가 있고, 수정본도 있을시에는 둘을 비교하여 추가하거나 삭제해준다
                     List<String> originalFileNames = new ArrayList<>();
                     List<String> updateFileNames = new ArrayList<>();
                     //기존 이미지의 파일명들을 추출해준다
                     while(originalMatcher.find()) {
                        String imageUrl = originalMatcher.group(1);
                        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                        originalFileNames.add(fileName);
                     }  
                     //수정본 이미지 파일명들을 추출과 src경로를 수정해준다
                     while(updateMatcher.find()) {
                        String imageUrl = updateMatcher.group(1);
                        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

                        updateContent = updateContent.replace(imageUrl, "/imagePath/posts/" + fileName);
                        updateFileNames.add(fileName);
                     }

                    //기존에만 있고 수정본엔 없는 파일은 삭제한다
                     List<String> onlyInOriginal = new ArrayList<>(originalFileNames);
                     onlyInOriginal.removeAll(updateFileNames); // 원본에만 있엇던거 (공통된 파일명)
                     
                     for(String fileName : onlyInOriginal) {
                        File tempOriginalFile = new File(tempUploadDir + fileName);
                        File originalFile = new File(newUploadDir + fileName);

                        tempOriginalFile.delete();
                        originalFile.delete();
                     }

                    //수정본에만 있고 기존에는 없는 파일을 새로 추가하여 준다
                     List<String> onlyUpdate = new ArrayList<>(updateFileNames);
                     onlyUpdate.removeAll(originalFileNames); // 수정본에만 있는거

                     for(String fileName : onlyUpdate) {
                        File tempFile = new File( tempUploadDir + fileName);
                        File newFile = new File(newUploadDir + fileName);
                        Files.move(tempFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                     }

                    return updateContent;

                }
            }
    }

    //게시물 삭제시 이미지를 삭제한다
    public void deleteImg(String content){

        Pattern pattern = Pattern.compile("!\\[.*?\\]\\((.*?)\\)");
        Matcher matchar = pattern.matcher(content);

        String uploadDir = "C:\\Users\\must1\\Desktop\\communityImg\\";

        while(matchar.find()){
                        String imageUrl = matchar.group(1);

                        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

                        File file = new File( uploadDir + fileName);
                        file.delete();
                    }

    }


    //스마트에디터 2 임시 이미지 업로드 메소드
    public String tempImgUpload(MultipartFile file) throws IOException{
            // 임시 저장폴더
            String uploadDir = "C:\\Users\\must1\\Desktop\\temp\\";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = timestamp + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            //최종 저장할 파일 생성
            File dest = new File(uploadDir+fileName);
            file.transferTo(dest);

            String imagePath =  "/imagePath/" + fileName;
            
            return imagePath;
    }

}
