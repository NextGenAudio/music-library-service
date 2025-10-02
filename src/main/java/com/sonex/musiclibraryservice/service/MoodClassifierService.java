package com.sonex.musiclibraryservice.service;

import com.sonex.musiclibraryservice.Kafka.PlaylistMoodProducer;
import com.sonex.musiclibraryservice.model.AudioUploadEvent;
import com.sonex.musiclibraryservice.model.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MoodClassifierService {

    private final PlaylistMoodProducer playlistMoodProducer;

    public MoodClassifierService(PlaylistMoodProducer playlistMoodProducer) {
        this.playlistMoodProducer = playlistMoodProducer;
    }
    // Send to the music mood classifier service
    public void sendAudioUploadEvent(FileInfo file) {
        AudioUploadEvent newEvent = new AudioUploadEvent(file.getId(),file.getPath());
        playlistMoodProducer.publishAudioUploaded(newEvent);
    }
}
