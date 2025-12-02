package com.reactpf.reactpf.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class RiotMatchTimeLineDto {
    @Getter
    @Setter
    private InfoTimeLineDto info;

    @Getter
    @Setter
    public static class InfoTimeLineDto {
        private Long frameInterval;
        private Long gameId;
        private List<FramesTimeLineDto> frames;
    }

    @Getter
    @Setter
    public static class FramesTimeLineDto {
        private int timestamp;
        private Map<String, ParticipantFrameDTO> participantFrames;
        private List<EventDTO> events;
    }

    @Getter
    @Setter
    public static class ParticipantFrameDTO {
        private int participantId;
        private int level;
        private int currentGold;
        private int totalGold;
        private int minionsKilled;
        private int jungleMinionsKilled;
    }

    @Getter
    @Setter
    public static class EventDTO {
        private Long timestamp;
        private String type;
        private int itemId;
        private String ItemName;
        private String ItemDescription;
        private Integer participantId;
        private Integer skillSlot;     
        private String levelUpType;

        public String getSkillKey() {
            
        if (skillSlot == null) return "";
        return switch (skillSlot) {
            case 1 -> "Q";
            case 2 -> "W";
            case 3 -> "E";
            case 4 -> "R";
            default -> "?";
        };
    }
    }
    
}
