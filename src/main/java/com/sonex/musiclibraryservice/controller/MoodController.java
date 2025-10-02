package com.sonex.musiclibraryservice.controller;

import com.sonex.musiclibraryservice.model.Mood;
import com.sonex.musiclibraryservice.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/moods")
public class MoodController {

    @Autowired
    private MoodService moodService;

    @GetMapping
    public ResponseEntity<List<Mood>> getAllFolders() {
        return ResponseEntity.ok(moodService.getAllMoods());
    }
}
