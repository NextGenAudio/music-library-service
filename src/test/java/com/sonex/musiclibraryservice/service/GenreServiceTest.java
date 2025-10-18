package com.sonex.musiclibraryservice.service;

import com.sonex.musiclibraryservice.model.Genre;
import com.sonex.musiclibraryservice.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GenreServiceTest {
    @Test
    void testGetAllGenres() {
        GenreRepository mockRepo = mock(GenreRepository.class);
        Genre genre1 = new Genre();
        genre1.setGenre("Rock");
        Genre genre2 = new Genre();
        genre2.setGenre("Jazz");
        List<Genre> genres = Arrays.asList(genre1, genre2);
        when(mockRepo.findAll()).thenReturn(genres);

        GenreService service = new GenreService(mockRepo);
        List<Genre> result = service.getAllGenres();

        assertEquals(2, result.size());
        assertEquals("Rock", result.get(0).getGenre());
        assertEquals("Jazz", result.get(1).getGenre());
        verify(mockRepo, times(1)).findAll();
    }
}
