package com.reactpf.reactpf.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemsDto {

    private Map<String,ItemDescriptionDto> data;
    
    @Getter
    @Setter
    public static class  ItemDescriptionDto{
        private String name;
        private String description;
    }



}
