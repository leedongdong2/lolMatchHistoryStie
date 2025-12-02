package com.reactpf.reactpf.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuneTreeDto {
    
    private int id;
    private String key;
    private String icon;
    private String name;
    private List<Slot> slots;

    @Getter
    @Setter
    public static class Slot {
        private List<Rune> runes;
    }

    @Getter
    @Setter
    public static class Rune {
        private int id;
        private String key;
        private String icon;
        private String name;
        private String shortDesc;
        private String longDesc;
        private String tooltip;
    }
}
