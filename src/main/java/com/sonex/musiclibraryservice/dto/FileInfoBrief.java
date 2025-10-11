package com.sonex.musiclibraryservice.dto;


import java.util.Map;

public class FileInfoBrief {
    private final Long id;
    private final String filename;
    private final String path;
    private final String title;
    private final String artist;
    private final String album;
    private final Map<String, Object> metadata;
//    private final Integer duration;
    private final Integer listenCount;
//    private final String genre;
//    private final String mood;
    private final Boolean isLiked;
//    private final String uploadedAt;

    public FileInfoBrief(Long id, String filename, String path, String title, String artist, String album, Map<String, Object> metadata, Integer listenCount, Boolean isLiked) {
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.metadata = metadata;
        this.listenCount = listenCount;
//        this.genre = genre.getGenre();
//        this.mood = mood.getMood();
        this.isLiked = isLiked;
//        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Integer getListenCount() {
        return listenCount;
    }
    public Boolean getLiked() {
        return isLiked;
    }
}
