package com.example.telegrampay.service;

import com.example.telegrampay.domain.BotInstance;
import com.example.telegrampay.domain.Chat;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.repository.ChatRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat registerChat(Creator creator, BotInstance botInstance, String telegramChatId, String title, String type) {
        Chat chat = new Chat();
        chat.setCreator(creator);
        chat.setBotInstance(botInstance);
        chat.setTelegramChatId(telegramChatId);
        chat.setTitle(title);
        chat.setType(type);
        return chatRepository.save(chat);
    }

    public List<Chat> listChats(Long creatorId) {
        return chatRepository.findByCreatorId(creatorId);
    }
}
