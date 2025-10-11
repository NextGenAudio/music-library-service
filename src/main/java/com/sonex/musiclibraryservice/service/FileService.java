package com.sonex.musiclibraryservice.service;

import com.sonex.musiclibraryservice.dto.FileInfoBrief;
import com.sonex.musiclibraryservice.dto.FileInfoMore;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
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

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    private S3Client s3Client;
    private final String bucketName = "sonex2";

    Artwork artwork = null;

    public FileService(FileRepository fileRepository ) {
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

    public FileInfo saveFile(MultipartFile file , String folderId) throws IOException {
        String userId = getCurrentUserId();
        System.out.println("user id " + userId);

        String filename = Paths.get(file.getOriginalFilename()).getFileName().toString();

        // ✅ Define S3 key: userId/folderId/filename
        String s3Key = userId + "/musics/" + filename;

        // ✅ Upload to S3
        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(inputStream, file.getSize())
            );
        }

        // ✅ Build S3 file URL
        String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;

        // Metadata variables
        Tag tag = null;
        AudioHeader header = null;
        Artwork artwork = null;

        try {
            File tempFile = File.createTempFile("upload-", filename);
            file.transferTo(tempFile); // Needed for jaudiotagger
            AudioFile audioFile = AudioFileIO.read(tempFile);
            tag = audioFile.getTag();
            header = audioFile.getAudioHeader();
            artwork = (tag != null) ? tag.getFirstArtwork() : null;
            tempFile.delete(); // cleanup
        } catch (Exception e) {
            System.out.println("Warning: Unable to read metadata: " + e.getMessage());
        }


        // ✅ Build FileInfo object
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename(filename);
        fileInfo.setPath(fileUrl); // ✅ S3 URL
        fileInfo.setUploadedAt(LocalDateTime.now());
        fileInfo.setFolderId(Long.parseLong(folderId));
        fileInfo.setUserId(userId);
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
        fileInfo.setUserId(userId); // now correctly tied to the current user



        return fileRepository.save(fileInfo);
    }




    public List<FileInfoBrief> listFiles(Long folderId) {
        String userId = getCurrentUserId();

        if (folderId != null) {
            // filter by both user and folder
            return fileRepository.findByUserIdAndFolderId(userId, folderId);
        }

        // if no folder specified → return all files of user
        return fileRepository.findByUserId(userId);
    }


    public List<FileInfo> favoriteFiles() {
        String userId = getCurrentUserId();
        return fileRepository.findByUserIdAndIsLikedTrue(userId);
    }

    public List<FileInfo> recentFiles() {
        String userId = getCurrentUserId();
        return fileRepository.findTop5ByUserIdOrderByLastListenedAtDesc(userId);
    }

    public FileInfoMore getMusicDetails(Long id) {
        String userId =  getCurrentUserId();
        FileInfoMore fileInfoMore = fileRepository.findFileInfoMoreById(id);
        // Ensure the file belongs to the logged-in user
//        if (!fileInfoMore.getUserId().equals(userId)) {
//            throw new SecurityException("You are not allowed to view this file");
//        }
        return fileInfoMore;
    }


    public Resource getFile(String filename) throws MalformedURLException {
        String userId = getCurrentUserId();
        System.out.println("File name " + filename);
        // ✅ Build the S3 key (same as when uploading)
        String s3Key = userId + "/musics/" + filename;

        // ✅ Download object as InputStream
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        // Return as Spring Resource
        return new InputStreamResource(s3Client.getObject(getObjectRequest));
    }


    public FileInfo addLike(Long id, Boolean like) {
        FileInfo fileInfo = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Music not found"));

        fileInfo.setLiked(like);

        return fileRepository.save(fileInfo);
    }

    public FileInfo updateScore(Long id, float score) {
        FileInfo fileInfo = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Music not found"));

        fileInfo.setXScore(score);

        return fileRepository.save(fileInfo);
    }

    public FileInfo updateListenCount(Long id, Long listenCount) {
        FileInfo fileInfo = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Music not found"));

        fileInfo.setListenCount(listenCount);

        return fileRepository.save(fileInfo);
    }

    public List<FileInfo> trendingFiles() {
        return fileRepository.findByUserIdAndYScoreGreaterThanOrderByYScoreDesc(getCurrentUserId(), 4);
    }

    public List<FileInfo> mostListenedFiles() {
        return fileRepository.findTop5ByUserIdOrderByListenCountDesc(getCurrentUserId());
    }

    public void deleteFile(Long id) {
        String userId = getCurrentUserId();

        FileInfo fileInfo = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Ensure the file belongs to the logged-in user
        if (!fileInfo.getUserId().equals(userId)) {
            throw new SecurityException("You are not allowed to delete this file");
        }

        String s3Key = userId + "/musics/" + fileInfo.getFilename();
        // Delete from S3
        try (S3Client s3 = S3Client.builder()
                .region(Region.AP_SOUTHEAST_1)
                .build()){
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key) // key = file path inside bucket
                    .build();

            s3.deleteObject(deleteObjectRequest);
            System.out.println("File deleted from S3: " + s3Key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }

        // Delete from DB
        fileRepository.delete(fileInfo);
    }

    public List<FileInfo> getRecommendations(String genre,String mood,String artist){
        String userId = getCurrentUserId();
        return fileRepository.getRecommendations(userId, genre, mood, artist);
    }

}
