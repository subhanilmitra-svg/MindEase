package com.mindease.mindease.repository;

import com.mindease.mindease.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findByUsername(String username); // used in getJournalsForUser

    List<Journal> findByUserIdOrderByTimestampDesc(Long id);
}