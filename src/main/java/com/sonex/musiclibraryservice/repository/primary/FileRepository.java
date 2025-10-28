package com.sonex.musiclibraryservice.repository.primary;

import com.sonex.musiclibraryservice.dto.FileInfoBrief;
import com.sonex.musiclibraryservice.dto.FileInfoMore;
import com.sonex.musiclibraryservice.model.primary.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileInfo, Long> {


    @Query("SELECT new com.sonex.musiclibraryservice.dto.FileInfoBrief(" +
            "f.id, f.filename, f.path, f.title, f.artist, f.album, f.metadata, " +
            "CAST(f.listenCount as integer), f.isLiked, f.artworkURL ) " +
            "FROM Musics f " +
            "WHERE f.userId = :userId")
    List<FileInfoBrief> findByUserId(@Param("userId") String userId);

    @Query("SELECT new com.sonex.musiclibraryservice.dto.FileInfoBrief(" +
            "f.id, f.filename, f.path, f.title, f.artist, f.album, f.metadata, " +
            "CAST(f.listenCount as integer), f.isLiked, f.artworkURL) " +
            "FROM Musics f " +
            "WHERE f.userId = :userId AND f.folderId = :folderId")
    List<FileInfoBrief> findByUserIdAndFolderId(@Param("userId") String userId, @Param("folderId") Long folderId);



    @Query(value = "SELECT * FROM musics WHERE user_id = :userId AND is_liked = true", nativeQuery = true)
    List<FileInfo> findByUserIdAndIsLikedTrue(@Param("userId") String userId);

    // Top 5 most recently listened files
    @Query(value = "SELECT * FROM musics WHERE user_id = :userId ORDER BY last_listened_at DESC NULLS LAST LIMIT 5", nativeQuery = true)
    List<FileInfo> findTop5ByUserIdOrderByLastListenedAtDesc(@Param("userId") String userId);

    // Top songs by Y-Score threshold (e.g., top recommendations)
    @Query("SELECT f FROM Musics f WHERE f.userId = :userId AND f.yScore > :yScoreThreshold ORDER BY f.yScore DESC")
    List<FileInfo> findByUserIdAndYScoreGreaterThanOrderByYScoreDesc(@Param("userId") String userId, @Param("yScoreThreshold") float yScoreThreshold);

    // Top 5 most listened songs
    @Query(value = "SELECT f FROM Musics f WHERE f.userId = :userId ORDER BY f.listenCount DESC NULL LAST LIMIT 5", nativeQuery = true)
    List<FileInfo> findTop5ByUserIdOrderByListenCountDesc(String userId);

    @Query(value = "SELECT mu.id, mu.album, mu.artist, mu.filename, mu.folder_id, mu.genre_id, mu.is_liked, mu.last_listened_at, mu.listen_count, mu.metadata, mu.mood_id, mu.path, mu.title, mu.uploaded_at, mu.user_id, mu.x_score, mu.y_score, " +
            "mu.artwork_url, " +
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

    @Query(value = "SELECT m.id, g.genre, mo.mood, " +
            "TO_CHAR(m.uploaded_at, 'YYYY-MM-DD HH24:MI:SS') " +
            "FROM musics m " +
            "LEFT JOIN genres g ON m.genre_id = g.id " +
            "LEFT JOIN moods mo ON m.mood_id = mo.id " +
            "WHERE m.id = :id", nativeQuery = true)
    FileInfoMore findFileInfoMoreById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE musics SET genre_id = :genreId WHERE id = :id", nativeQuery = true)
    int updateGenreIdById(@Param("id") Long id, @Param("genreId") Long genreId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE musics SET mood_id = :moodId WHERE id = :id", nativeQuery = true)
    int updateMoodIdById(@Param("id") Long id, @Param("moodId") Long moodId);

}
