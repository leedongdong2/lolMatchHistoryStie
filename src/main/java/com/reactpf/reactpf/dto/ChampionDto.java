package com.reactpf.reactpf.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChampionDto {

    
    private String type;
    private String format;
    private String version;
    
    @JsonProperty("data")//json필드명과 java변수명을 매핑해주는역활   이경우에는 사실 안써도 됨
    private Map<String, Champion> data;  // 챔피언 데이터를 맵으로  받음
    
    @Getter
    @Setter
    public static class Champion {
        private String id;
        private String key;
        private String name;
        private List<String> tags;
    }

}
