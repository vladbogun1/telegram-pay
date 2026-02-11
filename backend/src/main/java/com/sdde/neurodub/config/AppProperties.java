package com.sdde.neurodub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Storage storage = new Storage();
    private Tools tools = new Tools();

    public Storage getStorage() {
        return storage;
    }

    public Tools getTools() {
        return tools;
    }

    public static class Storage {
        private Path baseDir;

        public Path getBaseDir() {
            return baseDir;
        }

        public void setBaseDir(Path baseDir) {
            this.baseDir = baseDir;
        }
    }

    public static class Tools {
        private String sddeUnpackerPath;
        private String sddeTextToolPath;
        private String wwiseUtilPath;
        private String wwiseConsolePath;
        private String ffmpegPath;
        private String fileRedirectorPath;
        private String sound2wemPath;
        private String whisperServiceUrl;
        private String translateServiceUrl;
        private String ttsServiceUrl;

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

        public String getWhisperServiceUrl() {
            return whisperServiceUrl;
        }

        public void setWhisperServiceUrl(String whisperServiceUrl) {
            this.whisperServiceUrl = whisperServiceUrl;
        }

        public String getTranslateServiceUrl() {
            return translateServiceUrl;
        }

        public void setTranslateServiceUrl(String translateServiceUrl) {
            this.translateServiceUrl = translateServiceUrl;
        }

        public String getTtsServiceUrl() {
            return ttsServiceUrl;
        }

        public void setTtsServiceUrl(String ttsServiceUrl) {
            this.ttsServiceUrl = ttsServiceUrl;
        }
    }
}
