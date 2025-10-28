package com.sonex.musiclibraryservice.repository.primary;

import com.sonex.musiclibraryservice.model.primary.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByGenre(String genre);
}
