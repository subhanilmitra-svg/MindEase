package com.mindease.mindease.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tips")
public class Tip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String moodType; // e.g., "Stressed", "Sad", "Angry"

    @Column(nullable = false, length = 500)
    private String tipText; // The actual advice text

    private String visualIdentifier; // e.g., "listening_music", "breathing", "walking"

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMoodType() {
        return moodType;
    }

    public void setMoodType(String moodType) {
        this.moodType = moodType;
    }

    public String getTipText() {
        return tipText;
    }

    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    public String getVisualIdentifier() {
        return visualIdentifier;
    }

    public void setVisualIdentifier(String visualIdentifier) {
        this.visualIdentifier = visualIdentifier;
    }
}