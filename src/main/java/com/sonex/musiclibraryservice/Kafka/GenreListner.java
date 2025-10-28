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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    @Transactional("primaryTransactionManager")
    public void consume(GenreListnerEvent message) {
        try {
            LOGGER.info("Retrieved the genre event: fileId={}, genre={}", message.getFileId(), message.getGenre());

            // 1. Find or create genre
            Optional<Genre> optGenre = genreRepository.findByGenre(message.getGenre());
            Genre genre = optGenre.orElse(null);
            if (genre == null) {
                LOGGER.info("Genre '{}' not found, creating new genre", message.getGenre());
                genre = new Genre();
                genre.setGenre(message.getGenre());
                genre.setDescription("Auto-generated genre from classification");
                genre = genreRepository.save(genre);
                LOGGER.info("Created new genre: {} (id={})", genre.getGenre(), genre.getId());
            } else {
                LOGGER.info("Found existing genre: {} (id={})", genre.getGenre(), genre.getId());
            }

            // 2. Find file and set genre
            FileInfo fileInfo = fileRepository.findById(message.getFileId())
                    .orElseThrow(() -> new RuntimeException("File not found with ID: " + message.getFileId()));

            fileInfo.setGenre(genre);

            // 3. Save file
            int updated = fileRepository.updateGenreIdById(message.getFileId(), genre.getId());
            if (updated > 0) {
                LOGGER.info("Updated genre_id for file {} -> genre id {} (rows={})", message.getFileId(), genre.getId(), updated);
            } else {
                LOGGER.warn("No rows updated when setting genre_id for file {}", message.getFileId());
            }

            // Reload from DB to ensure the change is persisted
            FileInfo reloaded = fileRepository.findById(message.getFileId()).orElse(null);
            LOGGER.info("Reloaded file {} from DB, genre id in DB={} ", message.getFileId(), (reloaded != null && reloaded.getGenre() != null ? reloaded.getGenre().getId() : null));

            LOGGER.info("Successfully updated file {} with genre {}", message.getFileId(), message.getGenre());

        } catch (Exception e) {
            LOGGER.error("Error processing genre event for fileId={}, genre={}: {}",
                    message.getFileId(), message.getGenre(), e.getMessage(), e);
        }
    }
}
