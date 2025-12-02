package com.reactpf.reactpf.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityDto {
    private String tnName;
    private String content;
    private String title; 
    private String commentCount;
    private String nickname;
    private int views;
    
    private String searchOrder;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String prettyTime;
    private int seq;


    private int offset;
    private int limit;

    private int rowNum;
}
