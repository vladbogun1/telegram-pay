package com.sdde.neurodub.repo;

import com.sdde.neurodub.model.JobRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<JobRecord, Long> {
}
