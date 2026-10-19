package com.mindease.mindease.controller;

import com.mindease.mindease.model.Journal;
import com.mindease.mindease.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/journals")
public class JournalController {

    // FIX: DTO must be public static
    public static class UpdateJournalRequest {
        public String entryText;
        public String stickers;
    }

    @Autowired
    private JournalService journalService;

    // Method to SAVE a new journal entry
    @PostMapping
    public ResponseEntity<Journal> createJournal(@RequestBody Journal journal) {
        // Calls saveJournal with ONE Journal argument
        Journal savedJournal = journalService.saveJournal(journal);
        return ResponseEntity.ok(savedJournal);
    }

    @GetMapping
    public ResponseEntity<List<Journal>> getAllJournalsForUser() {
        List<Journal> journals = journalService.getJournalsForUser();
        return ResponseEntity.ok(journals);
    }

    // Method to UPDATE an existing journal entry
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJournal(@PathVariable Long id, @RequestBody UpdateJournalRequest updateRequest) {
        try {
            // Calls updateJournal with THREE arguments (ID, text, stickers)
            Journal updatedJournal = journalService.updateJournal(id, updateRequest.entryText, updateRequest.stickers);
            return ResponseEntity.ok(updatedJournal);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred during update.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJournal(@PathVariable Long id) {
        try {
            journalService.deleteJournal(id);
            return ResponseEntity.ok("Journal deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Could not delete journal entry: " + e.getMessage());
        }
    }
}