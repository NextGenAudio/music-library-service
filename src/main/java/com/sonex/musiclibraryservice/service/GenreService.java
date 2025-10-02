package com.sonex.musiclibraryservice.service;

import com.sonex.musiclibraryservice.model.Genre;
import com.sonex.musiclibraryservice.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {
    GenreRepository genreRepository;
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    List<Genre> getAllGenres(){
        return genreRepository.findAll();
    }
}
