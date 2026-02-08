package com.sdde.neurodub.service;

import com.sdde.neurodub.util.ChecksumUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScanService {

    public ScanResult scan(Path gamePath) throws IOException {
        if (!Files.exists(gamePath) || !Files.isDirectory(gamePath)) {
            return new ScanResult(gamePath.toString(), List.of(),
                    "Game path not found from backend runtime. If running in Docker, mount the game folder or use a host path accessible inside the container.");
        }
        List<FileStatus> files = new ArrayList<>();
        addIfExists(gamePath.resolve("Global.big"), files);
        addIfExists(gamePath.resolve("UI.big"), files);
        addIfExists(gamePath.resolve("data/Audio/SD2/English(US).pck"), files);
        addIfExists(gamePath.resolve("data/Audio/SD2/English.pck"), files);
        addIfExists(gamePath.resolve("data/Audio/SD2/English_US.pck"), files);
        return new ScanResult(gamePath.toString(), files, null);
    }

    private void addIfExists(Path path, List<FileStatus> files) throws IOException {
        if (Files.exists(path)) {
            files.add(new FileStatus(path.toString(), true, ChecksumUtil.sha256(path)));
        } else {
            files.add(new FileStatus(path.toString(), false, null));
        }
    }

    public record ScanResult(String gamePath, List<FileStatus> files, String warning) {
    }

    public record FileStatus(String path, boolean exists, String sha256) {
    }
}
