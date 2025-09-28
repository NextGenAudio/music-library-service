package com.sonex.musiclibraryservice.repository;

import com.sonex.musiclibraryservice.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileInfo, Long> {


    List<FileInfo> findByUserId(String userId);

    List<FileInfo> findByUserIdAndFolderId(String userId, Long folderId);

    List<FileInfo> findByUserIdAndIsLikedTrue(String userId);
}
