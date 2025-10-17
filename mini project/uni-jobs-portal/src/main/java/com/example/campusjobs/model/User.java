package com.example.campusjobs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String displayNameTh;

    @Column(nullable = false)
    private String displayNameEn;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String faculty;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = true)
    private String password;

    public User() {}
    
    // ID User
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // StudentID/EmployeeID
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // Display Name {TH}
    public String getDisplayNameTh() { return displayNameTh; }
    public void setDisplayNameTh(String displayNameTh) { this.displayNameTh = displayNameTh; }

    // Display Name {EN}
    public String getDisplayNameEn() { return displayNameEn; }
    public void setDisplayNameEn(String displayNameEn) { this.displayNameEn = displayNameEn; }

    // Email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Department
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    // Faculty
    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    // Role
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}