package com.example.telegrampay.service;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.domain.Entitlement;
import com.example.telegrampay.domain.EntitlementStatus;
import com.example.telegrampay.domain.Product;
import com.example.telegrampay.repository.EntitlementRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntitlementService {
    private final EntitlementRepository entitlementRepository;
    private final TelegramService telegramService;

    public EntitlementService(EntitlementRepository entitlementRepository, TelegramService telegramService) {
        this.entitlementRepository = entitlementRepository;
        this.telegramService = telegramService;
    }

    @Transactional
    public Entitlement grantAccess(Creator creator, Product product, String telegramUserId, Instant accessUntil, String botToken) {
        Entitlement entitlement = new Entitlement();
        entitlement.setCreator(creator);
        entitlement.setProduct(product);
        entitlement.setTelegramUserId(telegramUserId);
        entitlement.setChatId(product.getChat().getTelegramChatId());
        entitlement.setAccessUntil(accessUntil);
        entitlement.setStatus(EntitlementStatus.ACTIVE);
        entitlement = entitlementRepository.save(entitlement);
        String inviteLink = telegramService.createSingleUseInviteLink(botToken, product.getChat().getTelegramChatId(), accessUntil);
        entitlement.setInviteLink(inviteLink);
        entitlement.setUpdatedAt(Instant.now());
        entitlementRepository.save(entitlement);
        telegramService.sendMessage(botToken, telegramUserId, "Access granted! Join here: " + inviteLink);
        return entitlement;
    }

    @Transactional
    public void expireEntitlements() {
        Instant now = Instant.now();
        List<Entitlement> entitlements = entitlementRepository.findByStatusAndAccessUntilBefore(EntitlementStatus.ACTIVE, now);
        for (Entitlement entitlement : entitlements) {
            entitlement.setStatus(EntitlementStatus.EXPIRED);
            entitlement.setUpdatedAt(now);
            entitlementRepository.save(entitlement);
            String botToken = telegramService.resolveBotToken(entitlement.getProduct().getChat().getBotInstance());
            telegramService.removeUserFromChat(botToken, entitlement.getChatId(), entitlement.getTelegramUserId());
        }
    }

    @Transactional
    public void sendRenewalReminders() {
        Instant now = Instant.now();
        Instant start = now.plus(23, ChronoUnit.HOURS);
        Instant end = now.plus(25, ChronoUnit.HOURS);
        List<Entitlement> expiring = entitlementRepository.findByStatusAndAccessUntilBetween(EntitlementStatus.ACTIVE, start, end);
        for (Entitlement entitlement : expiring) {
            String botToken = telegramService.resolveBotToken(entitlement.getProduct().getChat().getBotInstance());
            telegramService.sendMessage(botToken, entitlement.getTelegramUserId(),
                "Your access expires in 24 hours. Tap Renew in the bot to continue.");
        }
    }
}
