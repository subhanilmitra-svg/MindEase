package com.mindease.mindease.service;

import com.mindease.mindease.model.User;
import com.mindease.mindease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional; // This import is used

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User updateDisplayName(String username, String displayName) {
        // Correctly find the user or throw an error
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        user.setDisplayName(displayName);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        // Correctly find the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // !! We will add logic to delete moods/journals later !!

        userRepository.delete(user);
    }
}