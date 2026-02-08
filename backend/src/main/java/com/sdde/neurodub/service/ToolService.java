package com.sdde.neurodub.service;

import com.sdde.neurodub.config.AppProperties;
import com.sdde.neurodub.model.ToolConfig;
import com.sdde.neurodub.repo.ToolConfigRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class ToolService {
    private final ToolConfigRepository repository;
    private final AppProperties appProperties;

    public ToolService(ToolConfigRepository repository, AppProperties appProperties) {
        this.repository = repository;
        this.appProperties = appProperties;
    }

    public ToolConfig getOrCreate() {
        return repository.findById(1L).orElseGet(() -> {
            ToolConfig config = new ToolConfig();
            config.setSddeUnpackerPath(appProperties.getTools().getSddeUnpackerPath());
            config.setSddeTextToolPath(appProperties.getTools().getSddeTextToolPath());
            config.setWwiseUtilPath(appProperties.getTools().getWwiseUtilPath());
            config.setWwiseConsolePath(appProperties.getTools().getWwiseConsolePath());
            config.setFfmpegPath(appProperties.getTools().getFfmpegPath());
            config.setFileRedirectorPath(appProperties.getTools().getFileRedirectorPath());
            config.setSound2wemPath(appProperties.getTools().getSound2wemPath());
            return repository.save(config);
        });
    }

    public ToolConfig update(ToolConfig incoming) {
        ToolConfig config = getOrCreate();
        config.setSddeUnpackerPath(incoming.getSddeUnpackerPath());
        config.setSddeTextToolPath(incoming.getSddeTextToolPath());
        config.setWwiseUtilPath(incoming.getWwiseUtilPath());
        config.setWwiseConsolePath(incoming.getWwiseConsolePath());
        config.setFfmpegPath(incoming.getFfmpegPath());
        config.setFileRedirectorPath(incoming.getFileRedirectorPath());
        config.setSound2wemPath(incoming.getSound2wemPath());
        return repository.save(config);
    }

    public Map<String, String> toolStatus() {
        ToolConfig config = getOrCreate();
        Map<String, String> status = new HashMap<>();
        status.put("sddeUnpacker", toolStatus(config.getSddeUnpackerPath()));
        status.put("sddeTextTool", toolStatus(config.getSddeTextToolPath()));
        status.put("wwiseUtil", toolStatus(config.getWwiseUtilPath()));
        status.put("wwiseConsole", toolStatus(config.getWwiseConsolePath()));
        status.put("ffmpeg", toolStatus(config.getFfmpegPath()));
        status.put("fileRedirector", toolStatus(config.getFileRedirectorPath()));
        status.put("sound2wem", toolStatus(config.getSound2wemPath()));
        return status;
    }

    private String toolStatus(String path) {
        if (path == null || path.isBlank()) {
            return "NOT_CONFIGURED";
        }
        Path toolPath = Path.of(path);
        return Files.exists(toolPath) ? "READY" : "MISSING";
    }
}
