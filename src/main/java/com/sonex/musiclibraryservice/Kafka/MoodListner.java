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
            topics = "audio.processed.mood",
            groupId = "music-mood-classifier",
            containerFactory = "moodKafkaListenerContainerFactory"
    )
    public void consume(MoodListnerEvent message) {
        try {
            LOGGER.info("Retrieved the mood event: fileId={}, mood={}", message.getFileId(), message.getMood());

            // 1. Find or create mood
            Mood mood = moodRepository.findByMood(message.getMood()).orElse(null);
            if (mood == null) {
                LOGGER.info("Mood '{}' not found, creating new mood", message.getMood());
                mood = new Mood();
                mood.setMood(message.getMood());
                mood.setDescription("Auto-generated mood from classification");
                mood = moodRepository.save(mood);
                LOGGER.info("Created new mood: {}", mood.getMood());
            }

            // 2. Find file and set mood
            FileInfo fileInfo = fileRepository.findById(message.getFileId())
                    .orElseThrow(() -> new RuntimeException("File not found with ID: " + message.getFileId()));

            fileInfo.setMood(mood);

            // 3. Save file
            fileRepository.save(fileInfo);

            LOGGER.info("Successfully updated file {} with mood {}", message.getFileId(), message.getMood());

        } catch (Exception e) {
            LOGGER.error("Error processing mood event for fileId={}, mood={}: {}",
                    message.getFileId(), message.getMood(), e.getMessage(), e);
            // Don't rethrow - this will prevent infinite retries
        }
    }
}
