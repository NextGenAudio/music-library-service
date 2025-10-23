package com.sonex.musiclibraryservice.repository.primary;

import com.sonex.musiclibraryservice.model.primary.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoodRepository extends JpaRepository<Mood, Long> {
    Optional<Mood> findByMood(String mood);
}
