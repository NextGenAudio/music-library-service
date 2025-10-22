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

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}
