package com.example.telegrampay.service;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.repository.CreatorRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreatorService {
    private final CreatorRepository creatorRepository;

    public CreatorService(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    public Creator createCreator(String name) {
        Creator creator = new Creator();
        creator.setName(name);
        creator.setApiKey(UUID.randomUUID().toString());
        return creatorRepository.save(creator);
    }
}
