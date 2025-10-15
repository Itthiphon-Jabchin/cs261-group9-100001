package com.example.campusjobs.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "applications")
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Job job;

    private String applicantUsername; // student username (email)

    private String fullName;
    private String studentId;
    private String email;
    private String phone;

    @Column(length = 4000)
    private String answerText;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    private Instant appliedAt = Instant.now();

    public Application(){}

    public Application(Job job, String applicantUsername, String fullName, String studentId, String email, String phone, String answerText){
        this.job = job;
        this.applicantUsername = applicantUsername;
        this.fullName = fullName;
        this.studentId = studentId;
        this.email = email;
        this.phone = phone;
        this.answerText = answerText;
    }

    public Long getId(){ return id; }
    public Job getJob(){ return job; }
    public String getApplicantUsername(){ return applicantUsername; }
    public String getFullName(){ return fullName; }
    public String getStudentId(){ return studentId; }
    public String getEmail(){ return email; }
    public String getPhone(){ return phone; }
    public String getAnswerText(){ return answerText; }
    public ApplicationStatus getStatus(){ return status; }
    public Instant getAppliedAt(){ return appliedAt; }

    public void setId(Long id){ this.id = id; }
    public void setJob(Job job){ this.job = job; }
    public void setApplicantUsername(String u){ this.applicantUsername = u; }
    public void setFullName(String n){ this.fullName = n; }
    public void setStudentId(String s){ this.studentId = s; }
    public void setEmail(String e){ this.email = e; }
    public void setPhone(String p){ this.phone = p; }
    public void setAnswerText(String a){ this.answerText = a; }
    public void setStatus(ApplicationStatus s){ this.status = s; }
    public void setAppliedAt(Instant t){ this.appliedAt = t; }
}
