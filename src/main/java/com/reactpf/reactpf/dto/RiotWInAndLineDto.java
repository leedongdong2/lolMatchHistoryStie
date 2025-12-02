package com.reactpf.reactpf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiotWInAndLineDto {
    private int gameCount;
    private int winCount;
    private int loseCount;

    private int topLineCount;
    private int midLineCount;
    private int jungleLineCount;
    private int supportLineCount;
    private int adCarryLineCount;
    
    private int topLinePercent;
    private int midLinePercent;
    private int jungleLinePercent;
    private int supportLinePercent;
    private int adCarryLinePercent;
    
}
