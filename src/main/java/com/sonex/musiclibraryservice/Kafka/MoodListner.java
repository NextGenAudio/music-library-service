package com.sonex.musiclibraryservice.Kafka;

import com.sonex.musiclibraryservice.model.FileInfo;
import com.sonex.musiclibraryservice.model.Mood;
import com.sonex.musiclibraryservice.model.MoodListnerEvent;
import com.sonex.musiclibraryservice.repository.FileRepository;
import com.sonex.musiclibraryservice.repository.MoodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MoodListner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoodListner.class);

    @Autowired
    private final MoodRepository moodRepository;
    @Autowired
    private final FileRepository fileRepository;

    public MoodListner(MoodRepository moodRepository,FileRepository fileRepository) {
        this.moodRepository = moodRepository;
        this.fileRepository = fileRepository;
    }
    @KafkaListener(
            topics = "mood.processed",
            groupId = "music-mood-classifier"
    )
    public void consume(MoodListnerEvent message) {


        LOGGER.info("Retrieved the mood event: fileId={}, mood={}", message.getFileId(), message.getMood());
        // Add your business logic here to handle the mood classification result

        // 1. Find mood by name
        Mood mood = moodRepository.findByMood(message.getMood())
                .orElseThrow(() -> new RuntimeException("Mood not found: " + message.getMood()));

        // 2. Create new music and set mood
        FileInfo fileInfo = fileRepository.findById(message.getFileId())
                .orElseThrow(() -> new RuntimeException("Music not found"));

        fileInfo.setMood(mood);

        // 3. Save music
        fileRepository.save(fileInfo);

    }
}
