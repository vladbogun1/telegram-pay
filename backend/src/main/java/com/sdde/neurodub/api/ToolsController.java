package com.sdde.neurodub.api;

import com.sdde.neurodub.model.ToolConfig;
import com.sdde.neurodub.service.ToolService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tools")
public class ToolsController {
    private final ToolService toolService;

    public ToolsController(ToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        return toolService.toolStatus();
    }

    @PostMapping("/config")
    public ToolConfig update(@RequestBody ToolConfig config) {
        return toolService.update(config);
    }
}
