package com.sonex.musiclibraryservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity(name="Musics")
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(nullable = false, name="filename")
    private String filename;

    @Column(name="user_id", nullable = false)
    private String userId;

    @Column(name="path")
    private String path;

    @Column(name="uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "title")
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name="album")
    private String album;

    @Column(name = "folder_id")
    private Long folderId;

    @Column(name = "is_liked")
    private boolean isLiked;

    @Column(name = "x_score")
    private float xScore;

    @Column(name = "y_score")
    private float yScore;

    @Column(name = "last_listened_at")
    private LocalDateTime lastListenedAt;

    @Column(name = "listen_count")
    private Long listenCount;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "mood_id")
    private Mood mood;

    @Column(name="metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
    public FileInfo() {
    }

    public FileInfo(Long id, String filename, String userId, String path, LocalDateTime uploadedAt, String title, String artist, String album, Long folderId, boolean isLiked, float xScore, float yScore, LocalDateTime lastListenedAt, Long listenCount, Genre genre, Mood mood, Map<String, Object> metadata) {
        this.id = id;
        this.filename = filename;
        this.userId = userId;
        this.path = path;
        this.uploadedAt = uploadedAt;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.folderId = folderId;
        this.isLiked = isLiked;
        this.xScore = xScore;
        this.yScore = yScore;
        this.lastListenedAt = lastListenedAt;
        this.listenCount = listenCount;
        this.genre = genre;
        this.mood = mood;
        this.metadata = metadata;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public float getXScore() {
        return xScore;
    }

    public void setXScore(float xScore) {
        this.xScore = xScore;
    }

    public float getyScore() {
        return yScore;
    }

    public void setyScore(float yScore) {
        this.yScore = yScore;
    }

    public LocalDateTime getLastListenedAt() {
        return lastListenedAt;
    }

    public void setLastListenedAt(LocalDateTime lastListenedAt) {
        this.lastListenedAt = lastListenedAt;
    }

    public Long getListenCount() {
        return listenCount;
    }

    public void setListenCount(Long listenCount) {
        this.listenCount = listenCount;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }


}
