package com.sonex.musiclibraryservice.service;


import com.sonex.musiclibraryservice.Kafka.PlaylistGenreProducer;
import com.sonex.musiclibraryservice.model.AudioUploadEvent;
import com.sonex.musiclibraryservice.model.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;




@Service
public class GenreClassifierService {
    private final PlaylistGenreProducer playlistGenreProducer;

    public GenreClassifierService(PlaylistGenreProducer playlistGenreProducer) {
        this.playlistGenreProducer = playlistGenreProducer;
    }
    // Send to the music mood classifier service
    public void sendAudioUploadEvent(FileInfo file) {
        AudioUploadEvent newEvent = new AudioUploadEvent(file.getId(),file.getPath());
        playlistGenreProducer.publishAudioUploaded(newEvent);
    }
}
