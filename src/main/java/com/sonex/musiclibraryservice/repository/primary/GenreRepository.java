package com.sonex.musiclibraryservice.repository.primary;

import com.sonex.musiclibraryservice.model.primary.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findByGenre(String genre);
}
