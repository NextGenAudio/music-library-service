package com.sonex.musiclibraryservice.config;

import com.sonex.musiclibraryservice.model.MoodListnerEvent;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.sonex.musiclibraryservice.model.GenreListnerEvent;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    private Map<String, Object> getCommonKafkaConfig() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "boot-ctavlxrz.c2.kafka-serverless.us-east-1.amazonaws.com:9098");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Increase timeouts for MSK Serverless
        configProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "120000");
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "5000");
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "600000");
        configProps.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, "540000");

        // Enable detailed logging
        configProps.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "300000");

        // MSK IAM Authentication
        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        configProps.put(SaslConfigs.SASL_MECHANISM, "AWS_MSK_IAM");
        configProps.put(SaslConfigs.SASL_JAAS_CONFIG, "software.amazon.msk.auth.iam.IAMLoginModule required;");
        configProps.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, "software.amazon.msk.auth.iam.IAMClientCallbackHandler");

        // AWS Region
        configProps.put("aws.region", "us-east-1");

        LOGGER.info("Kafka config initialized with bootstrap servers: {}",
                configProps.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));

        return configProps;
    }

    @Bean("genreConsumerFactory")
    public ConsumerFactory<String, GenreListnerEvent> genreConsumerFactory() {
        Map<String, Object> configProps = getCommonKafkaConfig();
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "music-genre-classifier");
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, GenreListnerEvent.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean("moodConsumerFactory")
    public ConsumerFactory<String, MoodListnerEvent> moodConsumerFactory() {
        Map<String, Object> configProps = getCommonKafkaConfig();
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "music-mood-classifier");
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MoodListnerEvent.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean("genreKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, GenreListnerEvent> genreKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GenreListnerEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(genreConsumerFactory());

        // Set acknowledgment mode
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Add error handler
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());

        LOGGER.info("Genre Kafka listener container factory initialized");
        return factory;
    }

    @Bean("moodKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MoodListnerEvent> moodKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MoodListnerEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(moodConsumerFactory());

        // Set acknowledgment mode
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Add error handler
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());

        LOGGER.info("Mood Kafka listener container factory initialized");
        return factory;
    }
}