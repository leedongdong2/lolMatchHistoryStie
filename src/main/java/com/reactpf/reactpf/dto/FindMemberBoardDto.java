package com.reactpf.reactpf.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindMemberBoardDto {
        private Long seq;
        private String tnName;
        private String nickname;
        private String content;
        private String title;
        private String matchType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int views;
        private String prettyTime;
        private String commentCount;

}
