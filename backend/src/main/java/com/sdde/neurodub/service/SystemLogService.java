package com.sdde.neurodub.service;

import com.sdde.neurodub.config.AppProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Service
public class SystemLogService {
    private final AppProperties appProperties;

    public SystemLogService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public List<String> tail(int lines) throws IOException {
        Path logPath = appProperties.getStorage().getBaseDir().resolve("logs").resolve("application.log");
        if (!Files.exists(logPath)) {
            return List.of("Log file not found yet: " + logPath);
        }
        List<String> allLines = Files.readAllLines(logPath);
        if (allLines.isEmpty()) {
            return Collections.emptyList();
        }
        int safeLines = Math.max(1, Math.min(lines, 2000));
        int start = Math.max(0, allLines.size() - safeLines);
        return allLines.subList(start, allLines.size());
    }
}

