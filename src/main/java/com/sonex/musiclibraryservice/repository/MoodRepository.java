package com.sonex.musiclibraryservice.repository;

import com.sonex.musiclibraryservice.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoodRepository extends JpaRepository<Mood, Long> {
    Optional<Mood> findByMood(String mood);
}
