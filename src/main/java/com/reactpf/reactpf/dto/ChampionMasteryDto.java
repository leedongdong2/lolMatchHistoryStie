package com.reactpf.reactpf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChampionMasteryDto {
    private long championId;
    private long lastPlayTime;
    private String lastPlayTimeFormat;
    private int championLevel;
    private int championPoints;
    private String championName;
    private String championKeyName;
}
