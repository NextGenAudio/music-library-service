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
}
