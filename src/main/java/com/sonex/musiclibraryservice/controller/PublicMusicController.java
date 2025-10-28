package com.sonex.musiclibraryservice.controller;

import com.sonex.musiclibraryservice.model.primary.PublicMusics;
import com.sonex.musiclibraryservice.service.PublicMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/music")
public class PublicMusicController {
    @Autowired
    PublicMusicService publicMusicService;

    // Publish a music (POST)
    @PostMapping("/publish")
    public ResponseEntity<PublicMusics> publishMusic(@RequestParam Long artistId,
                                                     @RequestParam String artistName,
                                                     @RequestParam Long musicId) {
        PublicMusics published = publicMusicService.publishMusic(artistId, artistName, musicId);
        return ResponseEntity.ok(published);
    }

    // Get a public music by its id (GET)
    @GetMapping("/{id}")
    public ResponseEntity<PublicMusics> getPublicMusic(@PathVariable Long id) {
        PublicMusics publicMusic = publicMusicService.getPublicMusic(id);
        return ResponseEntity.ok(publicMusic);
    }

    // Get all public musics for an artist (GET)
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<PublicMusics>> getArtistPublicMusics(@PathVariable Long artistId) {
        List<PublicMusics> musics = publicMusicService.getArtistPublicMusics(artistId);
        return ResponseEntity.ok(musics);
    }
}
