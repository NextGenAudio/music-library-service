package com.sonex.musiclibraryservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoodListnerEvent {
    private String mood;
    private Long fileId;

    public MoodListnerEvent() {
    }

    public MoodListnerEvent(String mood, Long fileId) {
        this.mood = mood;
        this.fileId = fileId;
    }
}
