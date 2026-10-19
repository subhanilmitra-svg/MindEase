package com.mindease.mindease.controller;

import com.mindease.mindease.JwtUtil; // Import from main package
import com.mindease.mindease.model.User;
import com.mindease.mindease.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // --- 1. DTO Classes moved INSIDE and made public static ---
    // This fixes the "class is exposed" errors.

    /**
     * Data Transfer Object for the login request body.
     */
    public static class LoginRequest {
        public String username;
        public String password;
    }

    /**
     * Data Transfer Object for the login response body.
     */
    public static class LoginResponse {
        public String token;
        public User user;

        public LoginResponse(String token, User user) {
            this.token = token;
            user.setPassword(null); // Never send the password back
            this.user = user;
        }
    }
    // --- End of DTO Classes ---


    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil; // Inject the JwtUtil

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User newUser = authService.registerUser(user);
            newUser.setPassword(null); // Don't return password
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- UPDATED LOGIN METHOD ---
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            User user = authService.loginUser(loginRequest.username, loginRequest.password);

            // Generate a token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());

            // Return the token and user details in the response
            return ResponseEntity.ok(new LoginResponse(token, user));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}