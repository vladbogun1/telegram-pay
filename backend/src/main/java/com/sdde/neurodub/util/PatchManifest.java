package com.sdde.neurodub.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatchManifest {
    private String patchId;
    private String gamePathHash;
    private Map<String, String> toolVersions;
    private List<TouchedFile> touchedFiles = new ArrayList<>();
    private List<RedirectorFile> redirectorFiles = new ArrayList<>();
    private Instant createdAt = Instant.now();

    public String getPatchId() {
        return patchId;
    }

    public void setPatchId(String patchId) {
        this.patchId = patchId;
    }

    public String getGamePathHash() {
        return gamePathHash;
    }

    public void setGamePathHash(String gamePathHash) {
        this.gamePathHash = gamePathHash;
    }

    public Map<String, String> getToolVersions() {
        return toolVersions;
    }

    public void setToolVersions(Map<String, String> toolVersions) {
        this.toolVersions = toolVersions;
    }

    public List<TouchedFile> getTouchedFiles() {
        return touchedFiles;
    }

    public void setTouchedFiles(List<TouchedFile> touchedFiles) {
        this.touchedFiles = touchedFiles;
    }

    public List<RedirectorFile> getRedirectorFiles() {
        return redirectorFiles;
    }

    public void setRedirectorFiles(List<RedirectorFile> redirectorFiles) {
        this.redirectorFiles = redirectorFiles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static class TouchedFile {
        private String path;
        private String sha256Before;
        private String sha256After;
        private String backupPath;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getSha256Before() {
            return sha256Before;
        }

        public void setSha256Before(String sha256Before) {
            this.sha256Before = sha256Before;
        }

        public String getSha256After() {
            return sha256After;
        }

        public void setSha256After(String sha256After) {
            this.sha256After = sha256After;
        }

        public String getBackupPath() {
            return backupPath;
        }

        public void setBackupPath(String backupPath) {
            this.backupPath = backupPath;
        }
    }

    public static class RedirectorFile {
        private String relativePath;
        private String sha256;

        public String getRelativePath() {
            return relativePath;
        }

        public void setRelativePath(String relativePath) {
            this.relativePath = relativePath;
        }

        public String getSha256() {
            return sha256;
        }

        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }
    }
}
