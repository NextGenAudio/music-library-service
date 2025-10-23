package com.sonex.musiclibraryservice.model.primary;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name = "user_id", nullable=false)
    private String userId;

    @Column(name="music_count", nullable=false)
    private int musicCount;

    @Column(name="description")
    private String description;

    @Column(name = "folder_art")
    private String folderArt;

    @Column(name= "created_at" , nullable=false)
    private LocalDateTime createdAt;

}
