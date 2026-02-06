package com.example.telegrampay.controller;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.dto.CreateCreatorRequest;
import com.example.telegrampay.dto.CreateCreatorResponse;
import com.example.telegrampay.service.CreatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/creators")
public class CreatorController {
    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCreatorResponse create(@Valid @RequestBody CreateCreatorRequest request) {
        Creator creator = creatorService.createCreator(request.getName());
        return new CreateCreatorResponse(creator.getId(), creator.getApiKey());
    }
}
