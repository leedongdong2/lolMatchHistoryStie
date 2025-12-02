package com.reactpf.reactpf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageDto {

        int page;
        int startPage;
        int totalPages;
        int endPage;

        public PageDto(int page,int pageSize,int totalCount){
            this.page = page;

            //총 페이지 수 구해주기
            this.totalPages = (int) Math.ceil((double) totalCount / pageSize); 
            
            // 페이지 그룹의 시작 번호
            this.startPage = ((page - 1) / 5) * 5 + 1;

             // 페이지 그룹의 끝 번호 (totalPages를 넘지 않도록)
            this.endPage = Math.min(startPage + 5 - 1, totalPages);
        }
}
