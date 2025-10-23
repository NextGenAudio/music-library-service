package com.sonex.musiclibraryservice.model.primary;

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

    @Column(name="artwork_url")
    private String artworkURL;
}
