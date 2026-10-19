package com.mindease.mindease.repository;

import com.mindease.mindease.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Import Optional

public interface UserRepository extends JpaRepository<User, Long> {
    // This method is required by AuthService and UserService
    Optional<User> findByUsername(String username);
}