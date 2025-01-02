package com.example.EPLS.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "images")
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imagePath; // Path to the image file

    @Column(nullable = false)
    private String title; // Title of the image

    @Column(length = 500)
    private String description; // Description or caption for the image

    @Column(nullable = false)
    private String uploadedBy; // User email or username who uploaded the image

    // Constructors
    public Images() {
    }

    public Images(String imagePath, String title, String description, String uploadedBy) {
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.uploadedBy = uploadedBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    @Override
    public String toString() {
        return "Images{" +
                "id=" + id +
                ", imagePath='" + imagePath + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", uploadedBy='" + uploadedBy + '\'' +
                '}';
    }
}
