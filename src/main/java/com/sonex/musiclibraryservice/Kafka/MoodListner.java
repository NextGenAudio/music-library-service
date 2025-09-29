package com.sonex.musiclibraryservice.Kafka;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MoodListner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoodListner.class);

    @KafkaListener(
            topics = "audio.processed",
            groupId = "music-mood-classifier",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(String message) {
        LOGGER.info("Retrieved the message: {}", message);
    }
}



