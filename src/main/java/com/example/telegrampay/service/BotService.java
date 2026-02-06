package com.example.telegrampay.service;

import com.example.telegrampay.domain.BotInstance;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.repository.BotInstanceRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BotService {
    private final BotInstanceRepository botInstanceRepository;
    private final CryptoService cryptoService;

    public BotService(BotInstanceRepository botInstanceRepository, CryptoService cryptoService) {
        this.botInstanceRepository = botInstanceRepository;
        this.cryptoService = cryptoService;
    }

    public BotInstance registerBot(Creator creator, String name, String botToken) {
        BotInstance bot = new BotInstance();
        bot.setCreator(creator);
        bot.setName(name);
        bot.setBotTokenEncrypted(cryptoService.encrypt(botToken));
        bot.setWebhookSecretToken(UUID.randomUUID().toString());
        return botInstanceRepository.save(bot);
    }

    public List<BotInstance> listBots(Long creatorId) {
        return botInstanceRepository.findByCreatorId(creatorId);
    }
}
