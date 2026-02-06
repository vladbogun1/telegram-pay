package com.example.telegrampay.controller;

import com.example.telegrampay.domain.BotInstance;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.dto.ChatResponse;
import com.example.telegrampay.dto.RegisterChatRequest;
import com.example.telegrampay.repository.BotInstanceRepository;
import com.example.telegrampay.service.ChatService;
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
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final CreatorLookupService creatorLookupService;
    private final BotInstanceRepository botInstanceRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatResponse register(@Valid @RequestBody RegisterChatRequest request) {
        Creator creator = creatorLookupService.currentCreator();
        BotInstance botInstance = botInstanceRepository.findById(request.getBotInstanceId())
            .filter(bot -> bot.getCreator().getId().equals(creator.getId()))
            .orElseThrow(() -> new IllegalArgumentException("Bot not found"));
        return DtoMapper.toChatResponse(
            chatService.registerChat(creator, botInstance, request.getTelegramChatId(), request.getTitle(), request.getType())
        );
    }

    @GetMapping
    public List<ChatResponse> list() {
        Creator creator = creatorLookupService.currentCreator();
        return chatService.listChats(creator.getId()).stream()
            .map(DtoMapper::toChatResponse)
            .toList();
    }
}
