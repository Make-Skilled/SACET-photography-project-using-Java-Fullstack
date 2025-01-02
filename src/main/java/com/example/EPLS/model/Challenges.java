package com.example.EPLS.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Challenges {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique identifier for the challenge

    @Column(name = "challenge_title", nullable = false)
    private String challengeTitle;  // Title of the challenge

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;  // Description of the challenge

    @Column(name = "image_path", nullable = false)
    private String imagePath;  // Path to the image associated with the challenge
    
    public Challenges() {
        // You can leave it empty or initialize default values if needed
    }



    // Constructor with parameters
    public Challenges(String challengeTitle, String description, String imagePath) {
        this.challengeTitle = challengeTitle;
        this.description = description;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", challengeTitle='" + challengeTitle + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

}
