package com.sdde.neurodub.service;

import com.sdde.neurodub.model.JobRecord;
import com.sdde.neurodub.repo.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public JobRecord createJob(Long projectId, String type) {
        JobRecord job = new JobRecord(projectId, type);
        job.setCreatedAt(Instant.now());
        job = jobRepository.save(job);
        return job;
    }

    public void updateJob(JobRecord job, String status, int progress, String message) {
        job.setStatus(status);
        job.setProgress(progress);
        job.setMessage(message);
        jobRepository.save(job);
        SseEmitter emitter = emitters.get(job.getId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("progress")
                        .data(new JobEvent(job.getId(), status, progress, message)));
                if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                    emitter.complete();
                    emitters.remove(job.getId());
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(job.getId());
            }
        }
    }

    public SseEmitter createEmitter(Long jobId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(jobId, emitter);
        emitter.onCompletion(() -> emitters.remove(jobId));
        emitter.onTimeout(() -> emitters.remove(jobId));
        return emitter;
    }

    public record JobEvent(Long jobId, String status, int progress, String message) {
    }
}
