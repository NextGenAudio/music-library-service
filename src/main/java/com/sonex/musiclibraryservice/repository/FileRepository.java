package com.sonex.musiclibraryservice.repository;

import com.sonex.musiclibraryservice.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileInfo, Long> {

    List<FileInfo> findByUserId(String userId);

    List<FileInfo> findByUserIdAndFolderId(String userId, Long folderId);

    List<FileInfo> findByUserIdAndIsLikedTrue(String userId);

    // Top 5 most recently listened files
    @Query(value = "SELECT * FROM musics WHERE user_id = :userId ORDER BY last_listened_at DESC NULLS LAST LIMIT 5", nativeQuery = true)
    List<FileInfo> findTop5ByUserIdOrderByLastListenedAtDesc(@Param("userId") String userId);

    // Top songs by Y-Score threshold (e.g., top recommendations)
    @Query("SELECT f FROM Musics f WHERE f.userId = :userId AND f.yScore > :yScoreThreshold ORDER BY f.yScore DESC")
    List<FileInfo> findByUserIdAndYScoreGreaterThanOrderByYScoreDesc(@Param("userId") String userId, @Param("yScoreThreshold") float yScoreThreshold);

    // Top 5 most listened songs
    List<FileInfo> findTop5ByUserIdOrderByListenCountDesc(String userId);

    @Query(value = "SELECT mu.id, mu.album, mu.artist, mu.filename, mu.folder_id, mu.genre_id, mu.is_liked, mu.last_listened_at, mu.listen_count, mu.metadata, mu.mood_id, mu.path, mu.title, mu.uploaded_at, mu.user_id, mu.x_score, mu.y_score, " +
            "(0.3 * (CASE WHEN (:currentMood IS NULL OR m.mood = :currentMood) THEN 1 ELSE 0 END)) +" +
            "(0.3 * (CASE WHEN (:currentGenre IS NULL OR g.genre = :currentGenre) THEN 1 ELSE 0 END)) +" +
            "(0.3 * (CASE WHEN (:currentArtist IS NULL OR mu.artist = :currentArtist) THEN 1 ELSE 0 END)) +" +
            "(0.1 * (mu.y_score)) -" +
            "(1 * exp(-extract(epoch from (now() - mu.last_listened_at)) / 86400 / 5)) AS recommendation_score " +
            "FROM musics mu " +
            "LEFT JOIN moods m ON mu.mood_id = m.id " +
            "LEFT JOIN genres g ON mu.genre_id = g.id " +
            "WHERE mu.user_id = :userId " +
            "ORDER BY recommendation_score DESC NULLS LAST " +
            "LIMIT 5", nativeQuery = true)
    List<FileInfo> getRecommendations(@Param("userId") String userId,
                                      @Param("currentGenre") String currentGenre,
                                      @Param("currentMood") String currentMood,
                                      @Param("currentArtist") String currentArtist);


}
