package com.mindease.mindease.service;

import com.mindease.mindease.model.Journal;
import com.mindease.mindease.model.User;
import com.mindease.mindease.repository.JournalRepository;
import com.mindease.mindease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Necessary import
import org.springframework.security.core.context.SecurityContextHolder; // Necessary import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalService {

    @Autowired private JournalRepository journalRepository;
    @Autowired private UserRepository userRepository;

    // Helper method to get the REAL logged-in user (prevents hardcoding crashes)
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found or session invalid."));
    }

    @Transactional
    public Journal saveJournal(Journal journal) {
        // FIX: Get the REAL user object
        User loggedInUser = getLoggedInUser();

        journal.setUser(loggedInUser);
        journal.setTimestamp(LocalDateTime.now());
        return journalRepository.save(journal);
    }

    public List<Journal> getJournalsForUser() {
        // FIX: Get the REAL user object
        User loggedInUser = getLoggedInUser();
        return journalRepository.findByUserIdOrderByTimestampDesc(loggedInUser.getId());
    }

    @Transactional
    public Journal updateJournal(Long journalId, String newText, String newStickers) {
        Long currentUserId = getLoggedInUser().getId(); // Get current user ID

        Journal existingJournal = journalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));

        // SECURITY CHECK: Ensure user owns the journal
        if (!existingJournal.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("User not authorized to update this journal");
        }

        existingJournal.setEntryText(newText);
        existingJournal.setStickers(newStickers);
        existingJournal.setTimestamp(LocalDateTime.now());
        return journalRepository.save(existingJournal);
    }

    @Transactional
    public void deleteJournal(Long journalId) {
        Long currentUserId = getLoggedInUser().getId();

        Journal existingJournal = journalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Journal entry not found with id: " + journalId));

        // SECURITY CHECK: Ensure user owns the journal
        if (!existingJournal.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("User not authorized to delete this journal");
        }

        journalRepository.delete(existingJournal);
    }
}