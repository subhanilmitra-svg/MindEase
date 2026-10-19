package com.mindease.mindease.service;

import com.mindease.mindease.model.Mood;
import com.mindease.mindease.model.User;
import com.mindease.mindease.repository.MoodRepository;
import com.mindease.mindease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Necessary import
import org.springframework.security.core.context.SecurityContextHolder; // Necessary import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MoodService {

    @Autowired private MoodRepository moodRepository;
    @Autowired private UserRepository userRepository;

    // Helper method to get the REAL logged-in user (prevents hardcoding crashes)
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found or session invalid."));
    }

    @Transactional
    public Mood saveMood(Mood mood) {
        // FIX: Get the REAL user object
        User loggedInUser = getLoggedInUser();

        mood.setUser(loggedInUser);
        mood.setTimestamp(LocalDateTime.now());
        return moodRepository.save(mood);
    }

    public List<Mood> getMoodsForUser() {
        // FIX: Get the REAL user object
        User loggedInUser = getLoggedInUser();
        return moodRepository.findByUserIdOrderByTimestampDesc(loggedInUser.getId());
    }

    @Transactional
    public void deleteMood(Long moodId) {
        Long currentUserId = getLoggedInUser().getId();

        Mood existingMood = moodRepository.findById(moodId)
                .orElseThrow(() -> new RuntimeException("Mood entry not found with id: " + moodId));

        // SECURITY CHECK: Ensure user owns the mood
        if (!existingMood.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("User not authorized to delete this mood");
        }

        moodRepository.delete(existingMood);
    }
}