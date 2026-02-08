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
            throw new IllegalArgumentException("Game path does not exist or is not a directory.");
        }
        List<FileStatus> files = new ArrayList<>();
        addIfExists(gamePath.resolve("Global.big"), files);
        addIfExists(gamePath.resolve("UI.big"), files);
        addIfExists(gamePath.resolve("data/Audio/SD2/English(US).pck"), files);
        addIfExists(gamePath.resolve("data/Audio/SD2/English.pck"), files);
        addIfExists(gamePath.resolve("data/Audio/SD2/English_US.pck"), files);
        return new ScanResult(gamePath.toString(), files);
    }

    private void addIfExists(Path path, List<FileStatus> files) throws IOException {
        if (Files.exists(path)) {
            files.add(new FileStatus(path.toString(), true, ChecksumUtil.sha256(path)));
        } else {
            files.add(new FileStatus(path.toString(), false, null));
        }
    }

    public record ScanResult(String gamePath, List<FileStatus> files) {
    }

    public record FileStatus(String path, boolean exists, String sha256) {
    }
}
