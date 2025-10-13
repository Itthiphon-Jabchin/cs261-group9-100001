package com.example.campusjobs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // บอก JPA ว่าให้สร้าง/ใช้ตารางชื่อ users
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username; // รหัสนักศึกษา/พนักงาน, ใช้เป็น unique key

    @Column(nullable = false)
    private String displayNameTh;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 20)
    private String role; // "ROLE_STUDENT" or "ROLE_TEACHER"

    // --- Constructors, Getters, Setters ---
    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayNameTh() { return displayNameTh; }
    public void setDisplayNameTh(String displayNameTh) { this.displayNameTh = displayNameTh; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}