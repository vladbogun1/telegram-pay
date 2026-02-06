package com.example.telegrampay.repository;

import com.example.telegrampay.domain.Creator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorRepository extends JpaRepository<Creator, Long> {
    Optional<Creator> findByApiKey(String apiKey);
}
