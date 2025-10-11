package com.sonex.musiclibraryservice.config;


import com.sonex.musiclibraryservice.model.MoodListnerEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.sonex.musiclibraryservice.model.GenreListnerEvent;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;


@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean("genreConsumerFactory")
    public ConsumerFactory<String, GenreListnerEvent> genreConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "music-genre-classifier");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, GenreListnerEvent.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean("moodConsumerFactory")
    public ConsumerFactory<String, MoodListnerEvent> moodConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "music-mood-classifier");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MoodListnerEvent.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean("genreKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, GenreListnerEvent> genreKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GenreListnerEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(genreConsumerFactory());
        return factory;
    }

    @Bean("moodKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MoodListnerEvent> moodKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MoodListnerEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(moodConsumerFactory());
        return factory;
    }
}