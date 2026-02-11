package com.sdde.neurodub.service;

import com.sdde.neurodub.model.SubtitleEntry;
import com.sdde.neurodub.repo.SubtitleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SubtitleService {
    private final SubtitleRepository repository;

    public SubtitleService(SubtitleRepository repository) {
        this.repository = repository;
    }

    public List<SubtitleEntry> extractSample(Long projectId) {
        repository.findByProjectId(projectId).forEach(repository::delete);
        List<SubtitleEntry> entries = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            entries.add(new SubtitleEntry(projectId, "EN_Gameplay", "LINE_" + i, "Sample subtitle line " + i + " from SDDE", "EN"));
        }
        return repository.saveAll(entries);
    }

    public List<SubtitleEntry> search(Long projectId, String search) {
        if (search == null || search.isBlank()) {
            return repository.findByProjectId(projectId);
        }
        return repository.findByProjectIdAndTextContainingIgnoreCase(projectId, search);
    }

    public List<SubtitleEntry> getAll(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    public SubtitleEntry saveMappingAsSubtitle(Long projectId, String text, String fileName) {
        SubtitleEntry entry = new SubtitleEntry(projectId, fileName, UUID.randomUUID().toString(), text, "RU");
        return repository.save(entry);
    }
}
