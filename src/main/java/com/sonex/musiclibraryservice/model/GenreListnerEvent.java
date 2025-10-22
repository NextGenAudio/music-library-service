package com.sonex.musiclibraryservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreListnerEvent {
    private String genre;
    private Long fileId;

    public GenreListnerEvent() {
    }

    public GenreListnerEvent(String genre, Long fileId) {
        this.genre = genre;
        this.fileId = fileId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}
