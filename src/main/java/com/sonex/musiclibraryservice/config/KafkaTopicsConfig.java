package com.sonex.musiclibraryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic audioUploadedGenre() {
        return TopicBuilder.name("audio.uploaded.genre").partitions(6).replicas(1).build();
    }

    @Bean
    public NewTopic audioUploadedMood() {
        return TopicBuilder.name("audio.uploaded.mood").partitions(6).replicas(1).build();
    }

    @Bean
    public NewTopic audioProcessedGenre() {
        return TopicBuilder.name("audio.processed.genre").partitions(6).replicas(1).build();
    }

    @Bean
    public NewTopic audioProcessedMood() {
        return TopicBuilder.name("audio.processed.mood").partitions(6).replicas(1).build();
    }
}
