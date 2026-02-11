package com.sdde.neurodub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdde.neurodub.config.AppProperties;
import com.sdde.neurodub.model.MappingEntry;
import com.sdde.neurodub.model.Project;
import com.sdde.neurodub.model.SubtitleEntry;
import com.sdde.neurodub.util.ChecksumUtil;
import com.sdde.neurodub.util.PatchManifest;
import com.sdde.neurodub.util.RedirectorPathMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PatchService {
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PatchService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public PatchManifest buildPatch(Project project, List<SubtitleEntry> subtitles, List<MappingEntry> mappings) throws IOException {
        String patchId = UUID.randomUUID().toString();
        Path patchDir = appProperties.getStorage().getBaseDir().resolve("patches").resolve(patchId);
        Files.createDirectories(patchDir);

        PatchManifest manifest = new PatchManifest();
        manifest.setPatchId(patchId);
        manifest.setGamePathHash(Integer.toHexString(project.getGamePath().hashCode()));
        manifest.setToolVersions(toolVersionSnapshot());
        manifest.setCreatedAt(Instant.now());

        for (MappingEntry mapping : mappings) {
            PatchManifest.TouchedFile touchedFile = new PatchManifest.TouchedFile();
            touchedFile.setPath("Audio/PCK/" + mapping.getWemId() + ".wem");
            touchedFile.setSha256Before("unknown");
            touchedFile.setSha256After("pending");
            touchedFile.setBackupPath(patchDir.resolve("backup").resolve(mapping.getWemId() + ".wem").toString());
            manifest.getTouchedFiles().add(touchedFile);
        }

        for (SubtitleEntry subtitle : subtitles) {
            PatchManifest.RedirectorFile redirectorFile = new PatchManifest.RedirectorFile();
            redirectorFile.setRelativePath("Text/" + subtitle.getFileName() + ".bin");
            redirectorFile.setSha256("pending");
            manifest.getRedirectorFiles().add(redirectorFile);
        }

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(patchDir.resolve("PatchManifest.json").toFile(), manifest);
        return manifest;
    }

    public ApplyResult applyPatch(Project project, PatchManifest manifest) throws IOException {
        Path gameRoot = Path.of(project.getGamePath());
        Path redirectorRoot = gameRoot.resolve("RedirectorData");
        Files.createDirectories(redirectorRoot);

        for (PatchManifest.RedirectorFile redirectorFile : manifest.getRedirectorFiles()) {
            Path targetPath = RedirectorPathMapper.mapToRedirector(gameRoot, Path.of(redirectorFile.getRelativePath()));
            Files.createDirectories(targetPath.getParent());
            Files.writeString(targetPath, "Placeholder redirector content for " + redirectorFile.getRelativePath());
            redirectorFile.setSha256(ChecksumUtil.sha256(targetPath));
        }

        return new ApplyResult("APPLIED", "Redirector files staged. Audio patching requires wwiseutil + WwiseConsole.");
    }

    public RollbackResult rollback(Project project, PatchManifest manifest) throws IOException {
        Path gameRoot = Path.of(project.getGamePath());
        int removed = 0;
        for (PatchManifest.RedirectorFile redirectorFile : manifest.getRedirectorFiles()) {
            Path targetPath = RedirectorPathMapper.mapToRedirector(gameRoot, Path.of(redirectorFile.getRelativePath()));
            if (Files.exists(targetPath)) {
                Files.delete(targetPath);
                removed++;
            }
        }
        return new RollbackResult("ROLLED_BACK", removed, "Redirector entries removed. Audio restore requires backup." );
    }

    private Map<String, String> toolVersionSnapshot() {
        Map<String, String> versions = new HashMap<>();
        versions.put("sdde-unpacker", "external");
        versions.put("sdde-text-tool", "external");
        versions.put("wwiseutil", "external");
        versions.put("wwise-console", "external");
        versions.put("ffmpeg", "external");
        return versions;
    }

    public record ApplyResult(String status, String message) {
    }

    public record RollbackResult(String status, int redirectorRemoved, String message) {
    }
}
