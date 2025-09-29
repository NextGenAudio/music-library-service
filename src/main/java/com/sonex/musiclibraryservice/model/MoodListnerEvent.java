package com.sonex.musiclibraryservice.model;

public class MoodListnerEvent {
    private String mood;
    private String fileId;

    public MoodListnerEvent(String mood, String fileId) {
        this.mood = mood;
        this.fileId = fileId;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
