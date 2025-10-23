package com.sonex.musiclibraryservice.Kafka;

import com.sonex.musiclibraryservice.model.primary.FileInfo;
import com.sonex.musiclibraryservice.model.primary.Genre;
import com.sonex.musiclibraryservice.model.primary.GenreListnerEvent;
import com.sonex.musiclibraryservice.repository.primary.FileRepository;
import com.sonex.musiclibraryservice.repository.primary.GenreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class GenreListner {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenreListner.class);

    @Autowired
    private final GenreRepository genreRepository;
    @Autowired
    private final FileRepository fileRepository;

    public GenreListner(GenreRepository genreRepository, FileRepository fileRepository) {
        this.genreRepository = genreRepository;
        this.fileRepository = fileRepository;
    }

    @KafkaListener(
            topics = "audio.processed.genre",
            groupId = "music-genre-classifier",
            containerFactory = "genreKafkaListenerContainerFactory"
    )
    public void consume(GenreListnerEvent message) {
        try {
            LOGGER.info("Retrieved the genre event: fileId={}, genre={}", message.getFileId(), message.getGenre());

            // 1. Find or create genre
            Genre genre = genreRepository.findByGenre(message.getGenre());
            if (genre == null) {
                LOGGER.info("Genre '{}' not found, creating new genre", message.getGenre());
                genre = new Genre();
                genre.setGenre(message.getGenre());
                genre.setDescription("Auto-generated genre from classification");
                genre = genreRepository.save(genre);
                LOGGER.info("Created new genre: {}", genre.getGenre());
            }

            // 2. Find file and set genre
            FileInfo fileInfo = fileRepository.findById(message.getFileId())
                    .orElseThrow(() -> new RuntimeException("File not found with ID: " + message.getFileId()));

            fileInfo.setGenre(genre);

            // 3. Save file
            fileRepository.save(fileInfo);

            LOGGER.info("Successfully updated file {} with genre {}", message.getFileId(), message.getGenre());

        } catch (Exception e) {
            LOGGER.error("Error processing genre event for fileId={}, genre={}: {}",
                    message.getFileId(), message.getGenre(), e.getMessage(), e);
        }
    }
}
