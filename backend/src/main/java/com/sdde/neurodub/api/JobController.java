package com.sdde.neurodub.api;

import com.sdde.neurodub.service.JobService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/{jobId}/events")
    public SseEmitter stream(@PathVariable Long jobId) {
        return jobService.createEmitter(jobId);
    }
}
