package com.sdde.neurodub.repo;

import com.sdde.neurodub.model.MappingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MappingRepository extends JpaRepository<MappingEntry, Long> {
    List<MappingEntry> findByProjectId(Long projectId);
}
