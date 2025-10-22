package com.sonex.musiclibraryservice.dto;

public class FileInfoMore {
    private final Long id;
    private final String genre;
    private final String mood;
    private final String uploadedAt;

    public FileInfoMore(Long id, String genre, String mood, String uploadedAt) {
        this.id = id;
        this.genre = genre;
        this.mood = mood;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public String getMood() {
        return mood;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

}
