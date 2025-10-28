package com.sonex.musiclibraryservice.Kafka;

import com.sonex.musiclibraryservice.model.primary.FileInfo;
import com.sonex.musiclibraryservice.model.primary.Mood;
import com.sonex.musiclibraryservice.model.primary.MoodListnerEvent;
import com.sonex.musiclibraryservice.repository.primary.FileRepository;
import com.sonex.musiclibraryservice.repository.primary.MoodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
                LOGGER.info("Created new mood: {} (id={})", mood.getMood(), mood.getId());
            }

            // 2. Atomically update mood_id in DB to avoid overwriting other fields
            int updated = fileRepository.updateMoodIdById(message.getFileId(), mood.getId());
            if (updated > 0) {
                LOGGER.info("Updated mood_id for file {} -> mood id {} (rows={})", message.getFileId(), mood.getId(), updated);
            } else {
                LOGGER.warn("No rows updated when setting mood_id for file {}", message.getFileId());
            }

            LOGGER.info("Successfully updated file {} with mood {}", message.getFileId(), message.getMood());

        } catch (Exception e) {
            LOGGER.error("Error processing mood event for fileId={}, mood={}: {}",
                    message.getFileId(), message.getMood(), e.getMessage(), e);
            // Don't rethrow - this will prevent infinite retries
        }
    }
}
