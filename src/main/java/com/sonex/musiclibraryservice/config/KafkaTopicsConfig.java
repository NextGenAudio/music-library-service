package com.sonex.musiclibraryservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


    @Configuration
    public class KafkaTopicsConfig {
        @Bean
        public NewTopic audioUploaded() {
            return TopicBuilder.name("audio.uploaded").partitions(6).replicas(1).build();
        }


    }
