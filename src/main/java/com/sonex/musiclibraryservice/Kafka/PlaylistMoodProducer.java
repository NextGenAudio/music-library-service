package com.sonex.musiclibraryservice.Kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonex.musiclibraryservice.model.AudioUploadEvent;
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
    private final KafkaTemplate <String,String> kafkaTemplate;

    public PlaylistMoodProducer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAudioUploaded(AudioUploadEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            Message<String> message = MessageBuilder.withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, "audio.uploaded")
                    .setHeader(KafkaHeaders.KEY, String.valueOf(event.fileId()))
                    .build();

            kafkaTemplate.send(message);
            LOGGER.info(String.format("Sending order event to topic %s",message.toString()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
