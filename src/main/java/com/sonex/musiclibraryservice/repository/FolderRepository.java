package com.sonex.musiclibraryservice.repository;

import com.sonex.musiclibraryservice.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
}
