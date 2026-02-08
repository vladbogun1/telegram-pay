package com.sdde.neurodub.repo;

import com.sdde.neurodub.model.SubtitleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubtitleRepository extends JpaRepository<SubtitleEntry, Long> {
    List<SubtitleEntry> findByProjectId(Long projectId);
    List<SubtitleEntry> findByProjectIdAndTextContainingIgnoreCase(Long projectId, String text);
}
