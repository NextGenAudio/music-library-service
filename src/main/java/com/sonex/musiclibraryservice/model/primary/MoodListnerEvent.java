package com.sonex.musiclibraryservice.model.primary;

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
