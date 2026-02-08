package com.sdde.neurodub.repo;

import com.sdde.neurodub.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
