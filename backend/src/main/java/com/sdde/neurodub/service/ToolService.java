package com.sdde.neurodub.service;

import com.sdde.neurodub.config.AppProperties;
import com.sdde.neurodub.model.ToolConfig;
import com.sdde.neurodub.repo.ToolConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class ToolService {
    private static final Logger log = LoggerFactory.getLogger(ToolService.class);
    private final ToolConfigRepository repository;
    private final AppProperties appProperties;

    public ToolService(ToolConfigRepository repository, AppProperties appProperties) {
        this.repository = repository;
        this.appProperties = appProperties;
    }

    public ToolConfig getOrCreate() {
        ToolConfig config = repository.findById(1L).orElseGet(ToolConfig::new);
        boolean changed = applyDefaultsIfMissing(config);
        if (config.getId() == null || changed) {
            ToolConfig saved = repository.save(config);
            if (changed) {
                log.info("Hydrated missing tool paths with defaults from application config");
            }
            return saved;
        }
        return config;
    }

    public ToolConfig update(ToolConfig incoming) {
        ToolConfig config = getOrCreate();
        config.setSddeUnpackerPath(preferIncoming(incoming.getSddeUnpackerPath(), appProperties.getTools().getSddeUnpackerPath()));
        config.setSddeTextToolPath(preferIncoming(incoming.getSddeTextToolPath(), appProperties.getTools().getSddeTextToolPath()));
        config.setWwiseUtilPath(preferIncoming(incoming.getWwiseUtilPath(), appProperties.getTools().getWwiseUtilPath()));
        config.setWwiseConsolePath(preferIncoming(incoming.getWwiseConsolePath(), appProperties.getTools().getWwiseConsolePath()));
        config.setFfmpegPath(preferIncoming(incoming.getFfmpegPath(), appProperties.getTools().getFfmpegPath()));
        config.setFileRedirectorPath(preferIncoming(incoming.getFileRedirectorPath(), appProperties.getTools().getFileRedirectorPath()));
        config.setSound2wemPath(preferIncoming(incoming.getSound2wemPath(), appProperties.getTools().getSound2wemPath()));
        log.info("Updated tool configuration");
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

    private boolean applyDefaultsIfMissing(ToolConfig config) {
        boolean changed = false;
        changed |= setIfBlank(config::getSddeUnpackerPath, config::setSddeUnpackerPath, appProperties.getTools().getSddeUnpackerPath());
        changed |= setIfBlank(config::getSddeTextToolPath, config::setSddeTextToolPath, appProperties.getTools().getSddeTextToolPath());
        changed |= setIfBlank(config::getWwiseUtilPath, config::setWwiseUtilPath, appProperties.getTools().getWwiseUtilPath());
        changed |= setIfBlank(config::getWwiseConsolePath, config::setWwiseConsolePath, appProperties.getTools().getWwiseConsolePath());
        changed |= setIfBlank(config::getFfmpegPath, config::setFfmpegPath, appProperties.getTools().getFfmpegPath());
        changed |= setIfBlank(config::getFileRedirectorPath, config::setFileRedirectorPath, appProperties.getTools().getFileRedirectorPath());
        changed |= setIfBlank(config::getSound2wemPath, config::setSound2wemPath, appProperties.getTools().getSound2wemPath());
        return changed;
    }

    private boolean setIfBlank(java.util.function.Supplier<String> getter,
                               java.util.function.Consumer<String> setter,
                               String defaultValue) {
        if ((getter.get() == null || getter.get().isBlank()) && defaultValue != null && !defaultValue.isBlank()) {
            setter.accept(defaultValue);
            return true;
        }
        return false;
    }

    private String preferIncoming(String incoming, String defaultValue) {
        if (incoming != null && !incoming.isBlank()) {
            return incoming;
        }
        return defaultValue;
    }
}
