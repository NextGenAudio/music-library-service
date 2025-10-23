package com.sonex.musiclibraryservice.service;


import com.sonex.musiclibraryservice.model.primary.Folder;
import com.sonex.musiclibraryservice.repository.primary.FolderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import net.coobird.thumbnailator.Thumbnails;
import com.sonex.musiclibraryservice.util.JwtUtil;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private S3Client s3Client;
    private final String bucketName = "sonex2";

    @Autowired
    private final JwtUtil jwtutil;

    public FolderService(JwtUtil jwtutil){
        this.jwtutil=jwtutil;
    }

    private String getCurrentUsername(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String username = "";
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            username = jwtutil.extractUsername(token);
        }
        return username;
    }
    public Folder createFolder(String name, String description, MultipartFile artwork, HttpServletRequest request) {

        String currentUsername = getCurrentUsername(request);
        Folder folder = new Folder();
        folder.setName(name);
        folder.setUserId(currentUsername);
        folder.setDescription(description);
        folder.setMusicCount(0);
        folder.setCreatedAt(LocalDateTime.now());

        if (artwork != null && !artwork.isEmpty()) {


            try {
                String uniqueFileName = System.currentTimeMillis() + "_" + artwork.getOriginalFilename();
                String s3Key = currentUsername + "/images/" + uniqueFileName;

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



    public List<Folder> getAllFolders(HttpServletRequest request) {
        String userId = getCurrentUsername(request);
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

    public void deleteFolder(Long id, HttpServletRequest request) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Folder not found with id " + id));
        if(!folder.getUserId().equals(getCurrentUsername(request))) {
            throw new SecurityException("Not authorized to delete this folder");
        }
        folderRepository.delete(folder);
    }
}
