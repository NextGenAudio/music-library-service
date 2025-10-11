package com.sonex.musiclibraryservice.repository;

import com.sonex.musiclibraryservice.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findByGenre(String genre);
}
