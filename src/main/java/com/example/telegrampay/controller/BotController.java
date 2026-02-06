package com.example.telegrampay.controller;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.dto.BotInstanceResponse;
import com.example.telegrampay.dto.RegisterBotRequest;
import com.example.telegrampay.dto.RegisterBotResponse;
import com.example.telegrampay.service.BotService;
import com.example.telegrampay.service.CreatorLookupService;
import com.example.telegrampay.service.DtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bots")
@RequiredArgsConstructor
public class BotController {
    private final BotService botService;
    private final CreatorLookupService creatorLookupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterBotResponse register(@Valid @RequestBody RegisterBotRequest request) {
        Creator creator = creatorLookupService.currentCreator();
        var bot = botService.registerBot(creator, request.getName(), request.getBotToken());
        return new RegisterBotResponse(bot.getId(), bot.getWebhookSecretToken());
    }

    @GetMapping
    public List<BotInstanceResponse> list() {
        Creator creator = creatorLookupService.currentCreator();
        return botService.listBots(creator.getId()).stream()
            .map(DtoMapper::toBotResponse)
            .toList();
    }
}
