package com.example.campusjobs.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(length = 4000, columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String creatorUsername; // อีเมลผู้สร้าง (อาจารย์)

    @Column(length = 2000, columnDefinition = "NVARCHAR(255)")
    private String questionPrompt;

    private Instant createdAt = Instant.now();

    public Job() {}

    public Job(String title, String description, String creatorUsername, String questionPrompt) {
        this.title = title;
        this.description = description;
        this.creatorUsername = creatorUsername;
        this.questionPrompt = questionPrompt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCreatorUsername() { return creatorUsername; }
    public String getQuestionPrompt() { return questionPrompt; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String t) { this.title = t; }
    public void setDescription(String d) { this.description = d; }
    public void setCreatorUsername(String u) { this.creatorUsername = u; }
    public void setQuestionPrompt(String q) { this.questionPrompt = q; }
    public void setCreatedAt(Instant i) { this.createdAt = i; }
}
