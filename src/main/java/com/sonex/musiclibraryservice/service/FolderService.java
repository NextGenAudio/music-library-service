package com.sonex.musiclibraryservice.service;


import com.sonex.musiclibraryservice.model.Folder;
import com.sonex.musiclibraryservice.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

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
        folder.setMusicCount(0); // new folder starts empty
        folder.setCreatedAt(LocalDateTime.now());

        if (artwork != null && !artwork.isEmpty()) {
            String userId = getCurrentUserId();

            try {
                String uploadDir = "E:/Sonex/Software Development/photos/uploads";
                String relativePath = "uploads/" + userId + "/";

                Path userDir = Paths.get(uploadDir, userId);

                Files.createDirectories(userDir); // ✅ ensure per-user folder exists

                // ✅ unique filename to prevent overwrites
                String uniqueFileName = System.currentTimeMillis() + "_" + artwork.getOriginalFilename();
                System.out.println("Original file: " + uniqueFileName);

                Path filePath = userDir.resolve(uniqueFileName);
                System.out.println("Saving to: " + filePath.toAbsolutePath());
                artwork.transferTo(filePath.toFile());

                // ✅ Save relative path (or absolute if needed)
                folder.setFolderArt(relativePath + uniqueFileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save artwork file", e);
            }
        }

        return folderRepository.save(folder);
    }



    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
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
