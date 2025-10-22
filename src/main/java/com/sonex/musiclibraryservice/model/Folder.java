package com.sonex.musiclibraryservice.model;


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

    @Column(name="musicCount", nullable=false)
    private int musicCount;

    @Column(name="description")
    private String description;

    @Column(name = "folder_art")
    private String folderArt;

    @Column(name= "created_at" , nullable=false)
    private LocalDateTime createdAt;

    public Folder() {
    }

    public Folder(Long id, String name, String userId, int musicCount, String description, String folderArt, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.musicCount = musicCount;
        this.description = description;
        this.folderArt = folderArt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(int musicCount) {
        this.musicCount = musicCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFolderArt() {
        return folderArt;
    }

    public void setFolderArt(String folderArt) {
        this.folderArt = folderArt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
