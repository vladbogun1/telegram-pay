package com.sdde.neurodub.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "jobs")
public class JobRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private String type;
    private String status;
    private int progress;

    @Column(length = 2000)
    private String message;

    private Instant createdAt = Instant.now();

    public JobRecord() {
    }

    public JobRecord(Long projectId, String type) {
        this.projectId = projectId;
        this.type = type;
        this.status = "PENDING";
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
