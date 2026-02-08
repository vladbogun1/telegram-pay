package com.sdde.neurodub.api;

import com.sdde.neurodub.model.MappingEntry;
import com.sdde.neurodub.model.Project;
import com.sdde.neurodub.model.SubtitleEntry;
import com.sdde.neurodub.repo.ProjectRepository;
import com.sdde.neurodub.service.*;
import com.sdde.neurodub.util.PatchManifest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final SubtitleService subtitleService;
    private final MappingService mappingService;
    private final PatchService patchService;
    private final TtsService ttsService;
    private final JobService jobService;

    public ProjectController(ProjectRepository projectRepository,
                             SubtitleService subtitleService,
                             MappingService mappingService,
                             PatchService patchService,
                             TtsService ttsService,
                             JobService jobService) {
        this.projectRepository = projectRepository;
        this.subtitleService = subtitleService;
        this.mappingService = mappingService;
        this.patchService = patchService;
        this.ttsService = ttsService;
        this.jobService = jobService;
    }

    @PostMapping
    public Project createProject(@RequestBody Map<String, String> request) {
        String name = request.getOrDefault("name", "SDDE Project");
        String gamePath = request.get("gamePath");
        return projectRepository.save(new Project(name, gamePath));
    }

    @PostMapping("/{id}/extract-subtitles")
    public List<SubtitleEntry> extractSubtitles(@PathVariable Long id) {
        jobService.createJob(id, "EXTRACT_SUBTITLES");
        return subtitleService.extractSample(id);
    }

    @GetMapping("/{id}/subtitles")
    public List<SubtitleEntry> listSubtitles(@PathVariable Long id, @RequestParam(required = false) String search) {
        return subtitleService.search(id, search);
    }

    @PostMapping("/{id}/mapping/auto")
    public List<MappingEntry> autoMapping(@PathVariable Long id) {
        return mappingService.autoMap(id);
    }

    @PostMapping("/{id}/mapping/manual")
    public MappingEntry manualMapping(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return mappingService.manualMap(id, request.get("wemId"), request.get("subtitleKey"), request.get("subtitleText"));
    }

    @PostMapping("/{id}/tts/preview")
    public ResponseEntity<byte[]> ttsPreview(@PathVariable Long id, @RequestBody Map<String, String> request) {
        byte[] audio = ttsService.preview(request.get("text"), request.getOrDefault("voice", "neutral_female"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=preview.wav")
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(audio);
    }

    @PostMapping("/{id}/build-patch")
    public PatchManifest buildPatch(@PathVariable Long id) throws IOException {
        Project project = projectRepository.findById(id).orElseThrow();
        List<SubtitleEntry> subtitles = subtitleService.getAll(id);
        List<MappingEntry> mappings = mappingService.list(id);
        return patchService.buildPatch(project, subtitles, mappings);
    }

    @PostMapping("/{id}/apply-patch")
    public PatchService.ApplyResult applyPatch(@PathVariable Long id, @RequestBody PatchManifest manifest) throws IOException {
        Project project = projectRepository.findById(id).orElseThrow();
        return patchService.applyPatch(project, manifest);
    }

    @PostMapping("/{id}/rollback")
    public PatchService.RollbackResult rollback(@PathVariable Long id, @RequestBody PatchManifest manifest) throws IOException {
        Project project = projectRepository.findById(id).orElseThrow();
        return patchService.rollback(project, manifest);
    }
}
