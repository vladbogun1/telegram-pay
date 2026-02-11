package com.sdde.neurodub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subtitles")
public class SubtitleEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private String fileName;
    private String subtitleKey;

    @Column(length = 4000)
    private String text;

    private String language;

    public SubtitleEntry() {
    }

    public SubtitleEntry(Long projectId, String fileName, String subtitleKey, String text, String language) {
        this.projectId = projectId;
        this.fileName = fileName;
        this.subtitleKey = subtitleKey;
        this.text = text;
        this.language = language;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSubtitleKey() {
        return subtitleKey;
    }

    public void setSubtitleKey(String subtitleKey) {
        this.subtitleKey = subtitleKey;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
