package com.reactpf.reactpf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankDto {
    
    String tier;
    String rank;
    short leaguePoints;
    short wins;
    short losses;
}
