package com.reactpf.reactpf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiotUserDto {
    
    private String puuid;
    private String gameName;
    private String tagLine;
    private String id;
    private String accountId;
    private long profileIconId;
    private long revisionDate;
    private short summonerLevel;
}
