package com.sonex.musiclibraryservice.controller;

import com.sonex.musiclibraryservice.service.FolderService;
import com.sonex.musiclibraryservice.Kafka.PlaylistMoodProducer;
import com.sonex.musiclibraryservice.model.AudioUploadEvent;
import com.sonex.musiclibraryservice.service.MoodClassifierService;
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
    private final MoodClassifierService moodClassifierService;
    @Autowired
    public FileController(FileService fileService , MoodClassifierService moodClassifierService) {
        this.fileService = fileService;
        this.moodClassifierService =  moodClassifierService;

    }

    @PostMapping("/upload")
    public ResponseEntity<FileInfo> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam(value = "folderId") String folderId) {
        System.out.println("folderid"+folderId);
        try {
            FileInfo savedFile = fileService.saveFile(file, folderId);
            // Call mood classification service
            moodClassifierService.sendAudioUploadEvent(savedFile);
            return ResponseEntity.ok(savedFile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(@RequestParam(required = false) Long folderId) {
        return ResponseEntity.ok(fileService.listFiles(folderId));
    }


    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = fileService.getFile( filename);

            String contentType = "audio/mpeg"; // or detect based on extension (.mp3, .wav, etc.)

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes") // ✅ enable seeking
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<FileInfo> likeMusic(
            @PathVariable Long id,
            @RequestParam("like") boolean like) {

        FileInfo updatedFile = fileService.addLike(id, like);
        if (updatedFile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedFile);
    }

    @PostMapping("/{id}/score")
    public ResponseEntity<FileInfo> updateScore(
            @PathVariable Long id,
            @RequestParam("score") float score) {

        FileInfo updatedFile = fileService.updateScore(id, score);
        if (updatedFile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedFile);
    }

    @PostMapping("/{id}/listen_count")
    public ResponseEntity<FileInfo> updateListenCount(
            @PathVariable Long id,
            @RequestParam("count") Long listenCount) {

        FileInfo updatedFile = fileService.updateListenCount(id, listenCount);
        if (updatedFile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedFile);
    }


    @GetMapping("/most-played")
    public ResponseEntity<List<FileInfo>> getMostListenedFiles() {

        List<FileInfo> mostPlayedMusics = fileService.mostListenedFiles();
        return ResponseEntity.ok(mostPlayedMusics);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<FileInfo>> getTrendingFiles() {

        List<FileInfo> trendingMusics = fileService.trendingFiles();
        return ResponseEntity.ok(trendingMusics);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorite")
    public ResponseEntity<List<FileInfo>> favoriteFiles() {
        return ResponseEntity.ok(fileService.favoriteFiles());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<FileInfo>> recentFiles() {
        return ResponseEntity.ok(fileService.recentFiles());
    }

}
