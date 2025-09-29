package com.sonex.musiclibraryservice.controller;

import com.sonex.musiclibraryservice.Kafka.PlaylistMoodProducer;
import com.sonex.musiclibraryservice.model.AudioUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import com.sonex.musiclibraryservice.model.FileInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.sonex.musiclibraryservice.service.FileService;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final PlaylistMoodProducer playlistMoodProducer;
    @Autowired
    public FileController(FileService fileService, PlaylistMoodProducer playlistMoodProducer) {
        this.fileService = fileService;
        this.playlistMoodProducer = playlistMoodProducer;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileInfo> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileInfo savedFile = fileService.saveFile(file);

            return ResponseEntity.ok(savedFile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/uploadfile")
    public String uploadFile(){
        AudioUploadEvent newEvent= new AudioUploadEvent("123","D:\\Projects\\Sonex\\music-classifier-service\\music-classifier-service\\sampletracks\\sample.mp3");
        playlistMoodProducer.publishAudioUploaded(newEvent);
        return "hrii";
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(@RequestParam(required = false) Long folderId) {
        return ResponseEntity.ok(fileService.listFiles(folderId));
    }


    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = fileService.getFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
