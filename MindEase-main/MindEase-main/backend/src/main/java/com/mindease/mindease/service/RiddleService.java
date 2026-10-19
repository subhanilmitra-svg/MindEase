package com.mindease.mindease.service;

import com.mindease.mindease.model.Riddle;
import com.mindease.mindease.repository.RiddleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RiddleService {

    @Autowired
    private RiddleRepository riddleRepository;

    public Optional<Riddle> getRandomRiddle() {
        // Use the custom query from RiddleRepository
        return riddleRepository.findRandomRiddle();
    }

    // Optional: Add a method to save riddles later
    // public Riddle saveRiddle(Riddle riddle) {
    //     return riddleRepository.save(riddle);
    // }
}