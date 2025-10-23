package com.sonex.musiclibraryservice.repository.primary;

import com.sonex.musiclibraryservice.model.primary.PublicMusics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicMusicRepository extends JpaRepository<PublicMusics, Long> {
    List<PublicMusics> findByArtistId(Long artistId);
}
