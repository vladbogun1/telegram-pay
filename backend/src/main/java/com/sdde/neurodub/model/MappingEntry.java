package com.sdde.neurodub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mappings")
public class MappingEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private String wemId;
    private String subtitleKey;

    @Column(length = 4000)
    private String subtitleText;

    private double confidence;
    private String status;

    public MappingEntry() {
    }

    public MappingEntry(Long projectId, String wemId, String subtitleKey, String subtitleText, double confidence, String status) {
        this.projectId = projectId;
        this.wemId = wemId;
        this.subtitleKey = subtitleKey;
        this.subtitleText = subtitleText;
        this.confidence = confidence;
        this.status = status;
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

    public String getWemId() {
        return wemId;
    }

    public void setWemId(String wemId) {
        this.wemId = wemId;
    }

    public String getSubtitleKey() {
        return subtitleKey;
    }

    public void setSubtitleKey(String subtitleKey) {
        this.subtitleKey = subtitleKey;
    }

    public String getSubtitleText() {
        return subtitleText;
    }

    public void setSubtitleText(String subtitleText) {
        this.subtitleText = subtitleText;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
