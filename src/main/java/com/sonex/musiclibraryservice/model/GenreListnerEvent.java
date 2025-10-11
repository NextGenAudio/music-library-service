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
}
