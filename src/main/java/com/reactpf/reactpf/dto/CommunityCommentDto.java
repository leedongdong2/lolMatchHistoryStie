package com.reactpf.reactpf.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommunityCommentDto{
    private Long seq;
    private Long postSeq;
    private String content;
    private String author;
    private Long parentSeq;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int depth;
    private String prettyTime;
    private List<CommunityCommentDto> children = new ArrayList<>();
    
}