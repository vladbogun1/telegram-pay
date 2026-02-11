package com.sdde.neurodub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tool_config")
public class ToolConfig {
    @Id
    private Long id = 1L;

    private String sddeUnpackerPath;
    private String sddeTextToolPath;
    private String wwiseUtilPath;
    private String wwiseConsolePath;
    private String ffmpegPath;
    private String fileRedirectorPath;
    private String sound2wemPath;

    public Long getId() {
        return id;
    }

    public String getSddeUnpackerPath() {
        return sddeUnpackerPath;
    }

    public void setSddeUnpackerPath(String sddeUnpackerPath) {
        this.sddeUnpackerPath = sddeUnpackerPath;
    }

    public String getSddeTextToolPath() {
        return sddeTextToolPath;
    }

    public void setSddeTextToolPath(String sddeTextToolPath) {
        this.sddeTextToolPath = sddeTextToolPath;
    }

    public String getWwiseUtilPath() {
        return wwiseUtilPath;
    }

    public void setWwiseUtilPath(String wwiseUtilPath) {
        this.wwiseUtilPath = wwiseUtilPath;
    }

    public String getWwiseConsolePath() {
        return wwiseConsolePath;
    }

    public void setWwiseConsolePath(String wwiseConsolePath) {
        this.wwiseConsolePath = wwiseConsolePath;
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public String getFileRedirectorPath() {
        return fileRedirectorPath;
    }

    public void setFileRedirectorPath(String fileRedirectorPath) {
        this.fileRedirectorPath = fileRedirectorPath;
    }

    public String getSound2wemPath() {
        return sound2wemPath;
    }

    public void setSound2wemPath(String sound2wemPath) {
        this.sound2wemPath = sound2wemPath;
    }
}
