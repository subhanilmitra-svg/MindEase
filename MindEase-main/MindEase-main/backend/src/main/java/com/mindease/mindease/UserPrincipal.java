package com.mindease.mindease;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.mindease.mindease.model.User;
import com.mindease.mindease.repository.UserRepository;

@Component
public class UserPrincipal {

    // This class helps us get the full User object of the logged-in user

    private final UserRepository userRepository;

    public UserPrincipal(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }

    public Long getLoggedInUserId() {
        return getLoggedInUser().getId();
    }
}
