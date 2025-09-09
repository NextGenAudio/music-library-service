package com.sonex.musiclibraryservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sonex.musiclibraryservice.model.FileInfo;
import com.sonex.musiclibraryservice.repository.FileRepository;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final String uploadDir = "E:/Sonex/Software Development/songs/uploads"; // server folder


    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;

    }
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Not authenticated");
        }

        // Get the user ID (UUID you set in your filter)
        return auth.getName();
    }

    public FileInfo saveFile(MultipartFile file) throws IOException {
        String userId = getCurrentUserId();
        System.out.println("user id " + userId);

        // Create per-user directory
        Path userDir = Paths.get(uploadDir, userId);
        Files.createDirectories(userDir); // ✅ ensures folder exists

        // Clean file name
        String filename = Paths.get(file.getOriginalFilename()).getFileName().toString();

        // Define storage path inside user’s folder
        Path filePath = userDir.resolve(filename);

        // Save file to disk
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Metadata variables
        Tag tag = null;
        AudioHeader header = null;
        Artwork artwork = null;

        try {
            AudioFile audioFile = AudioFileIO.read(filePath.toFile());
            tag = audioFile.getTag();
            header = audioFile.getAudioHeader();
            artwork = (tag != null) ? tag.getFirstArtwork() : null;
        } catch (Exception e) {
            System.out.println("Warning: Unable to read metadata: " + e.getMessage());
        }

        // Build FileInfo object
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename(filename);
        fileInfo.setPath(filePath.toAbsolutePath().toString());
        fileInfo.setUploadedAt(LocalDateTime.now());

        Map<String, Object> metadata = new HashMap<>();

        if (tag != null) {
            fileInfo.setTitle(tag.getFirst(FieldKey.TITLE));
            fileInfo.setArtist(tag.getFirst(FieldKey.ARTIST));
            fileInfo.setAlbum(tag.getFirst(FieldKey.ALBUM));

            for (FieldKey key : FieldKey.values()) {
                if (key == FieldKey.TITLE || key == FieldKey.ARTIST || key == FieldKey.ALBUM) continue;
                String value = tag.getFirst(key);
                if (value != null && !value.isEmpty()) {
                    String cleanValue = value.replace("\u0000", "").trim();
                    metadata.put(key.name().toLowerCase(), cleanValue);
                }
            }
        }

        if (header != null) {
            metadata.put("format", header.getFormat());
            metadata.put("bitrate", header.getBitRate());
            metadata.put("sample_rate", header.getSampleRate());
            metadata.put("channels", header.getChannels());
            metadata.put("track_length", header.getTrackLength());
        }

        // ✅ Handle cover art
        if (artwork != null) {
            try {
                byte[] imageData = artwork.getBinaryData();
                String base64Image = Base64.getEncoder().encodeToString(imageData);
                metadata.put("cover_art", "data:" + artwork.getMimeType() + ";base64," + base64Image);
            } catch (Exception e) {
                System.out.println("Warning: Unable to extract artwork: " + e.getMessage());
            }
        }

        fileInfo.setMetadata(metadata); // Save as JSON in DB
        fileInfo.setUserId(userId); // ✅ now correctly tied to the current user

        return fileRepository.save(fileInfo);
    }




    public List<FileInfo> listFiles(Long folderId) {
        String userId = getCurrentUserId();

        if (folderId != null) {
            // filter by both user and folder
            return fileRepository.findByUserIdAndFolderId(userId, folderId);
        }

        // if no folder specified → return all files of user
        return fileRepository.findByUserId(userId);
    }


    public Resource getFile(String filename) throws MalformedURLException {
        String userId = getCurrentUserId();
        Path userDir = Paths.get(uploadDir, userId);
        Path path = userDir.resolve(filename);
        return new UrlResource(path.toUri());
    }
}

