package com.sonex.musiclibraryservice.service;

import com.sonex.musiclibraryservice.Kafka.PlaylistMoodProducer;
import com.sonex.musiclibraryservice.model.primary.AudioUploadEvent;
import com.sonex.musiclibraryservice.model.primary.FileInfo;
import org.springframework.stereotype.Service;

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
