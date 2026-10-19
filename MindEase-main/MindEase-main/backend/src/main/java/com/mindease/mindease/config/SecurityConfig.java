package com.mindease.mindease.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // Inject the filter

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .cors(withDefaults()) // Enable CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use JWT, not sessions

                .authorizeHttpRequests(auth -> auth
                        // Allow public access to signup and login
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()

                        // Require authentication for all other API endpoints
                        .requestMatchers("/api/moods/**").authenticated()
                        .requestMatchers("/api/journals/**").authenticated()
                        .requestMatchers("/api/tips/**").authenticated()
                        .requestMatchers("/api/riddles/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated() // Secure the settings endpoints

                        .anyRequest().authenticated() // Secure any other request
                )
                // Add our filter to run before the default security filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}