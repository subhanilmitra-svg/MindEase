package com.mindease.mindease.service;

import com.mindease.mindease.model.Tip;
import com.mindease.mindease.model.User;
import com.mindease.mindease.repository.TipRepository;
import com.mindease.mindease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Necessary import
import org.springframework.security.core.context.SecurityContextHolder; // Necessary import
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TipService {

    @Autowired
    private TipRepository tipRepository;
    @Autowired
    private UserRepository userRepository;

    // Helper method to get the REAL logged-in user (prevents hardcoding crashes)
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found or session invalid."));
    }

    // This method remains simple since tips aren't per user, but it still requires a valid session
    public Optional<Tip> getRandomTipByMood(String moodType) {
        // We still need the user just to validate the session is alive
        getLoggedInUser();
        return tipRepository.findRandomTipByMoodType(moodType);
    }
}