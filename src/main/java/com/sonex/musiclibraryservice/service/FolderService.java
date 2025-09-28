package com.sonex.musiclibraryservice.service;


import com.sonex.musiclibraryservice.model.Folder;
import com.sonex.musiclibraryservice.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import net.coobird.thumbnailator.Thumbnails;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private S3Client s3Client;
    private final String bucketName = "sonex2";

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Not authenticated");
        }

        // Get the user ID (UUID you set in your filter)
        return auth.getName();
    }
    public Folder createFolder(String name, String description, MultipartFile artwork) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setUserId(this.getCurrentUserId());
        folder.setDescription(description);
        folder.setMusicCount(0);
        folder.setCreatedAt(LocalDateTime.now());

        if (artwork != null && !artwork.isEmpty()) {
            String userId = getCurrentUserId();

            try {
                String uniqueFileName = System.currentTimeMillis() + "_" + artwork.getOriginalFilename();
                String s3Key = userId + "/images/" + uniqueFileName;

                // ✅ Compress image in-memory
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(artwork.getInputStream())
                        .size(800, 800)       // resize (max 800px)
                        .outputQuality(0.7)   // reduce quality for smaller file
                        .toOutputStream(outputStream);

                byte[] compressedBytes = outputStream.toByteArray();

                // ✅ Upload compressed file to S3 (make public)
                try (InputStream inputStream = new ByteArrayInputStream(compressedBytes)) {
                    s3Client.putObject(
                            PutObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(s3Key)
                                    .contentType(artwork.getContentType())
                                    .build(),
                            RequestBody.fromInputStream(inputStream, compressedBytes.length)
                    );
                }

                // ✅ Store full public URL in DB
                String publicUrl = "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;
                folder.setFolderArt(publicUrl);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload artwork to S3", e);
            }
        }

        return folderRepository.save(folder);
    }



    public List<Folder> getAllFolders() {
        String userId = getCurrentUserId();
        return folderRepository.findByUserId(userId);
    }

    public Folder getFolderById(Long id) {
        return folderRepository.findById(id).orElse(null);
    }

    public Folder updateFolder(Long id, Folder updatedFolder) {
        return folderRepository.findById(id)
                .map(folder -> {
                    folder.setName(updatedFolder.getName());
                    return folderRepository.save(folder);
                })
                .orElse(null);
    }

    public boolean deleteFolder(Long id) {
        return folderRepository.findById(id)
                .map(folder -> {
                    folderRepository.delete(folder);
                    return true;
                })
                .orElse(false);
    }
}
