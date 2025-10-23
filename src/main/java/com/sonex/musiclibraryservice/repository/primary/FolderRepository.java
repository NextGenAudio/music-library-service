package com.sonex.musiclibraryservice.repository.primary;

import com.sonex.musiclibraryservice.model.primary.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserId(String folderName);
}
