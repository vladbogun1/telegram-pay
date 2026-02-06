package com.example.telegrampay.service;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.repository.CreatorRepository;
import com.example.telegrampay.security.CreatorContext;
import org.springframework.stereotype.Service;

@Service
public class CreatorLookupService {
    private final CreatorRepository creatorRepository;

    public CreatorLookupService(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    public Creator currentCreator() {
        Long creatorId = CreatorContext.getCreatorId();
        if (creatorId == null) {
            throw new IllegalStateException("Creator context missing");
        }
        return creatorRepository.findById(creatorId)
            .orElseThrow(() -> new IllegalStateException("Creator not found"));
    }
}
