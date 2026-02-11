package com.sdde.neurodub.api;

import com.sdde.neurodub.service.SystemLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    private final SystemLogService systemLogService;

    public SystemController(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @GetMapping("/logs")
    public Map<String, List<String>> logs(@RequestParam(defaultValue = "200") int lines) throws IOException {
        return Map.of("lines", systemLogService.tail(lines));
    }
}

