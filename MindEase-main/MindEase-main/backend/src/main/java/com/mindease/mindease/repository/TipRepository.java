package com.mindease.mindease.repository;

import com.mindease.mindease.model.Tip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TipRepository extends JpaRepository<Tip, Long> {

    // Find all tips for a specific mood type
    List<Tip> findByMoodType(String moodType);

    // Custom query to find one random tip for a specific mood type
    @Query(value = "SELECT * FROM tips WHERE mood_type = :moodType ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Tip> findRandomTipByMoodType(@Param("moodType") String moodType);
}
