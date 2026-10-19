package com.mindease.mindease.controller;

import com.mindease.mindease.model.Tip;
import com.mindease.mindease.service.TipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tips")
// No @CrossOrigin needed here because WebConfig handles it
public class TipController {

    @Autowired
    private TipService tipService;

    // Endpoint to get a random tip for a specific mood
    // Example URL: /api/tips/Stressed
    @GetMapping("/{moodType}")
    public ResponseEntity<?> getRandomTip(@PathVariable String moodType) {
        Optional<Tip> tipOptional = tipService.getRandomTipByMood(moodType);

        if (tipOptional.isPresent()) {
            return ResponseEntity.ok(tipOptional.get());
        } else {
            // If no tip is found for that mood, return a helpful message
            return ResponseEntity.notFound().build();
        }
    }

    /*
    // Endpoint to save a new tip (for adding data later)
    @PostMapping
    public ResponseEntity<Tip> createTip(@RequestBody Tip tip) {
        Tip savedTip = tipService.saveTip(tip);
        return ResponseEntity.ok(savedTip);
    }
    */
}
