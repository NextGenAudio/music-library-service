package com.sonex.musiclibraryservice.service;

import com.sonex.musiclibraryservice.model.Mood;
import com.sonex.musiclibraryservice.repository.MoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoodService {
    MoodRepository moodRepository;
    public MoodService(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }
    public List<Mood> getAllMoods(){
        return moodRepository.findAll();
    }
}
