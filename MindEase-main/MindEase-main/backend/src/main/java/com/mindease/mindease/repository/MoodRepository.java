package com.mindease.mindease.repository;

import com.mindease.mindease.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoodRepository extends JpaRepository<Mood, Long> {
    // Find all moods recorded by a specific user, ordered by timestamp
    List<Mood> findByUserIdOrderByTimestampDesc(Long userId);
}