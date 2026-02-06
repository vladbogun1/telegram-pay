package com.example.telegrampay.controller;

import com.example.telegrampay.domain.BotInstance;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.dto.TelegramUpdate;
import com.example.telegrampay.repository.BotInstanceRepository;
import com.example.telegrampay.service.AuditLogService;
import com.example.telegrampay.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/telegram")
public class TelegramWebhookController {
    private final BotInstanceRepository botInstanceRepository;
    private final PaymentService paymentService;
    private final AuditLogService auditLogService;
    public TelegramWebhookController(BotInstanceRepository botInstanceRepository,
                                     PaymentService paymentService,
                                     AuditLogService auditLogService) {
        this.botInstanceRepository = botInstanceRepository;
        this.paymentService = paymentService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/{botInstanceId}")
    @ResponseStatus(HttpStatus.OK)
    public void receive(@PathVariable Long botInstanceId,
                        @RequestBody TelegramUpdate update,
                        HttpServletRequest request) {
        BotInstance botInstance = botInstanceRepository.findById(botInstanceId)
            .orElseThrow(() -> new IllegalArgumentException("Bot not found"));
        String secret = request.getHeader("X-Telegram-Bot-Api-Secret-Token");
        if (secret == null || !secret.equals(botInstance.getWebhookSecretToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid webhook secret");
        }
        Creator creator = botInstance.getCreator();
        auditLogService.log(creator, "webhook.telegram", "botInstanceId=" + botInstanceId);

        if (update.getPreCheckoutQuery() != null) {
            paymentService.handlePreCheckout(botInstance, update.getPreCheckoutQuery());
            return;
        }
        if (update.getMessage() != null && update.getMessage().getFrom() != null) {
            String telegramUserId = update.getMessage().getFrom().getId().toString();
            if (update.getMessage().getSuccessfulPayment() != null) {
                paymentService.handleSuccessfulPayment(botInstance, creator,
                    update.getMessage().getSuccessfulPayment(), telegramUserId);
                return;
            }
            if (update.getMessage().getText() != null && update.getMessage().getText().startsWith("/paysupport")) {
                paymentService.handlePaySupport(creator, botInstance, telegramUserId);
            }
        }
    }
}
