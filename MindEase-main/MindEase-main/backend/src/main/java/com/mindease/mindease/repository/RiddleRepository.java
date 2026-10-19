package com.mindease.mindease.repository;

import com.mindease.mindease.model.Riddle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RiddleRepository extends JpaRepository<Riddle, Long> {

    // Custom query to find one random riddle from the table
    @Query(value = "SELECT * FROM riddles ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Riddle> findRandomRiddle();
}
