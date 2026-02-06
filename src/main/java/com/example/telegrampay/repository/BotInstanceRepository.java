package com.example.telegrampay.repository;

import com.example.telegrampay.domain.BotInstance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotInstanceRepository extends JpaRepository<BotInstance, Long> {
    List<BotInstance> findByCreatorId(Long creatorId);
}
