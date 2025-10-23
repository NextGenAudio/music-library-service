package com.sonex.musiclibraryservice.Kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonex.musiclibraryservice.model.primary.AudioUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class PlaylistMoodProducer {
    private static final Logger LOGGER= LoggerFactory.getLogger("producer for get mood");

    private final ObjectMapper objectMapper;
    private final KafkaTemplate <String,Object> kafkaTemplate;

    public PlaylistMoodProducer(ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAudioUploaded(AudioUploadEvent event) {
        try {
            Message<AudioUploadEvent> message = MessageBuilder.withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, "audio.uploaded.mood")
                    .setHeader(KafkaHeaders.KEY, String.valueOf(event.fileId()))
                    .build();

            kafkaTemplate.send(message);
            LOGGER.info("Sending audio upload event to topic audio.uploaded.mood: {}", event);

        } catch (Exception e) {
            LOGGER.error("Failed to send message", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
