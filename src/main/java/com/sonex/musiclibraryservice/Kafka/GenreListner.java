package com.sonex.musiclibraryservice.Kafka;

import com.sonex.musiclibraryservice.model.FileInfo;
import com.sonex.musiclibraryservice.model.Genre;
import com.sonex.musiclibraryservice.model.GenreListnerEvent;
import com.sonex.musiclibraryservice.repository.FileRepository;
import com.sonex.musiclibraryservice.repository.GenreRepository;
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
            topics = "genre.processed",
            groupId = "music-genre-classifier"
    )
    public void consume(GenreListnerEvent message) {
        LOGGER.info("Retrieved the genre event: fileId={}, genre={}", message.getFileId(), message.getGenre());

//        // 1. Find genre by name
//        Genre genre = genreRepository.findByGenreName(message.getGenre());
//        if (genre == null) {
//            throw new RuntimeException("Genre not found: " + message.getGenre());
//        }
//
//        // 2. Find file and set genre
//        FileInfo fileInfo = fileRepository.findById(message.getFileId())
//                .orElseThrow(() -> new RuntimeException("File not found"));
//
//        fileInfo.setGenre(genre);
//
//        // 3. Save file
//        fileRepository.save(fileInfo);
    }
}
