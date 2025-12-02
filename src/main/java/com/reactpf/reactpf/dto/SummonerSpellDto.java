package com.reactpf.reactpf.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummonerSpellDto {
    private String type;
    private String version;

    private Map<String,SummonerSpell> data;

    @Getter
    @Setter
    public static class SummonerSpell {
        private String id;
        private String name;
        private String description;
        private String key;
    }

}
