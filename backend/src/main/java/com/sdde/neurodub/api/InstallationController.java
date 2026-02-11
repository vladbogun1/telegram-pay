package com.sdde.neurodub.api;

import com.sdde.neurodub.service.ScanService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/installations")
public class InstallationController {
    private final ScanService scanService;

    public InstallationController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/scan")
    public ScanService.ScanResult scan(@RequestBody Map<String, String> request) throws IOException {
        String path = request.get("path");
        return scanService.scan(Path.of(path));
    }
}
