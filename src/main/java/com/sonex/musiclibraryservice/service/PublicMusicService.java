package com.sonex.musiclibraryservice.service;


import com.sonex.musiclibraryservice.model.primary.FileInfo;
import com.sonex.musiclibraryservice.model.primary.PublicMusics;
import com.sonex.musiclibraryservice.repository.primary.FileRepository;
import com.sonex.musiclibraryservice.repository.primary.PublicMusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PublicMusicService {

    @Autowired
    PublicMusicRepository publicMusicRepository;

    @Autowired
    FileRepository fileRepository;

    public PublicMusics publishMusic(Long artistId, String artistName , Long musicId) {
        PublicMusics publicMusics = new PublicMusics();
        publicMusics.setArtistId(artistId);

        FileInfo fileInfo = fileRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + musicId));

        publicMusics.setArtistName(artistName);
        publicMusics.setMusic(fileInfo);
        publicMusics.setPublishedAt(OffsetDateTime.now());
        return publicMusicRepository.save(publicMusics);

    }

    public PublicMusics getPublicMusic(Long id) {
        return publicMusicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Public music not found with ID: " + id));
    }

    public List<PublicMusics> getArtistPublicMusics(Long artistId) {
        return publicMusicRepository.findByArtistId(artistId);
    }

}
