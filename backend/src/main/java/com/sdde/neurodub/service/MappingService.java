package com.sdde.neurodub.service;

import com.sdde.neurodub.model.MappingEntry;
import com.sdde.neurodub.model.SubtitleEntry;
import com.sdde.neurodub.repo.MappingRepository;
import com.sdde.neurodub.repo.SubtitleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MappingService {
    private final MappingRepository mappingRepository;
    private final SubtitleRepository subtitleRepository;

    public MappingService(MappingRepository mappingRepository, SubtitleRepository subtitleRepository) {
        this.mappingRepository = mappingRepository;
        this.subtitleRepository = subtitleRepository;
    }

    public List<MappingEntry> autoMap(Long projectId) {
        mappingRepository.findByProjectId(projectId).forEach(mappingRepository::delete);
        List<SubtitleEntry> subtitles = subtitleRepository.findByProjectId(projectId);
        List<MappingEntry> mappings = new ArrayList<>();
        int counter = 1;
        for (SubtitleEntry entry : subtitles) {
            mappings.add(new MappingEntry(projectId, "WEM_" + counter, entry.getSubtitleKey(), entry.getText(), 0.65, "PENDING"));
            counter++;
        }
        return mappingRepository.saveAll(mappings);
    }

    public MappingEntry manualMap(Long projectId, String wemId, String subtitleKey, String subtitleText) {
        MappingEntry entry = new MappingEntry(projectId, wemId, subtitleKey, subtitleText, 1.0, "CONFIRMED");
        return mappingRepository.save(entry);
    }

    public List<MappingEntry> list(Long projectId) {
        return mappingRepository.findByProjectId(projectId);
    }
}
