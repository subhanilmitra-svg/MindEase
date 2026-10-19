package com.mindease.mindease.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journals")
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // links to the logged-in user

    @Lob // Good for long text
    @Column(columnDefinition = "TEXT")
    private String entryText;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY) // Use Lazy Fetching for optimization
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- 1. ADD THIS NEW FIELD ---
    @Lob // Use Lob/TEXT for long JSON strings
    @Column(columnDefinition = "TEXT")
    private String stickers; // Stores JSON string of sticker data

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // --- 2. ADD GETTER AND SETTER FOR STICKERS ---
    public String getStickers() {
        return stickers;
    }

    public void setStickers(String stickers) {
        this.stickers = stickers;
    }

}
