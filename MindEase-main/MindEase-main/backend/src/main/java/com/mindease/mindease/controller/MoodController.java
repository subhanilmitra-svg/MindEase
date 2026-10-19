package com.mindease.mindease.controller;

import com.mindease.mindease.model.Mood;
import com.mindease.mindease.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List; // <-- Make sure List is imported

@RestController
@RequestMapping("/api/moods")
// No @CrossOrigin needed here because WebConfig handles it
public class MoodController {

    @Autowired
    private MoodService moodService;

    // Method to SAVE a new mood
    @PostMapping
    public ResponseEntity<Mood> createMood(@RequestBody Mood mood) {
        Mood savedMood = moodService.saveMood(mood);
        return ResponseEntity.ok(savedMood);
    }

    // Method to GET all moods for the user
    @GetMapping
    public ResponseEntity<List<Mood>> getAllMoodsForUser() {
        List<Mood> moods = moodService.getMoodsForUser();
        return ResponseEntity.ok(moods);
    }
    // Add this method to MoodController.java
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMood(@PathVariable Long id) {
        try {
            moodService.deleteMood(id);
            return ResponseEntity.ok("Mood deleted successfully");
        } catch (Exception e) {
            // Handle cases where the mood doesn't exist or deletion fails
            return ResponseEntity.badRequest().body("Could not delete mood: " + e.getMessage());
        }
    }
}