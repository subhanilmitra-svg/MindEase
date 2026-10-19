package com.mindease.mindease.controller;

import com.mindease.mindease.model.User;
import com.mindease.mindease.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Necessary import
import org.springframework.security.core.context.SecurityContextHolder; // Necessary import
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    public static class DisplayNameRequest {
        public String displayName;
    }

    @Autowired
    private UserService userService;

    // Helper method to get the logged-in user's name (Fixes the NullPointerException crash)
    private String getLoggedInUsername() {
        // Retrieve the Authentication object that our JwtRequestFilter placed here
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is actually authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated or session expired.");
        }
        return authentication.getName();
    }

    // Endpoint to update display name
    @PutMapping("/me/displayname")
    public ResponseEntity<?> updateDisplayName(@RequestBody DisplayNameRequest request) {
        try {
            // FIX: Get the REAL username from the security context
            String username = getLoggedInUsername();

            User updatedUser = userService.updateDisplayName(username, request.displayName);
            updatedUser.setPassword(null); // Remove password
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to delete account
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount() {
        try {
            // FIX: Get the REAL username from the security context
            String username = getLoggedInUsername();
            userService.deleteUser(username);
            return ResponseEntity.ok("User account deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}