package com.mindease.mindease.controller;

import com.mindease.mindease.model.Riddle;
import com.mindease.mindease.service.RiddleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/riddles")
public class RiddleController {

    @Autowired
    private RiddleService riddleService;

    // Endpoint to get one random riddle
    @GetMapping("/random")
    public ResponseEntity<?> getRandomRiddle() {
        Optional<Riddle> riddleOptional = riddleService.getRandomRiddle();

        if (riddleOptional.isPresent()) {
            return ResponseEntity.ok(riddleOptional.get());
        } else {
            // If the table is empty
            return ResponseEntity.notFound().build();
        }
    }
}