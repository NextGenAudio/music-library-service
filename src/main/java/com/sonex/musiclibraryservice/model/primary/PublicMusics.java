package com.sonex.musiclibraryservice.model.primary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table(name = "public_musics")
public class PublicMusics {
    @Id
    @Column(name="id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "music_id", nullable = false, unique = true)
    private FileInfo music;

    @Column(name="artist_id", nullable = false)
    private Long artistId;

    @Column(name ="artist_name")
    private String artistName;

    @Column(name = "likes")
    private Long likes;

    @Column(name = "plays")
    private Long plays;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;
}
