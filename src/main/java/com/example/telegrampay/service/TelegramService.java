package com.example.telegrampay.service;

import com.example.telegrampay.domain.BotInstance;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {
    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);
    private static final String API_URL = "https://api.telegram.org/bot";

    private final RestTemplate restTemplate;
    private final CryptoService cryptoService;

    public TelegramService(RestTemplate restTemplate, CryptoService cryptoService) {
        this.restTemplate = restTemplate;
        this.cryptoService = cryptoService;
    }

    public String resolveBotToken(BotInstance botInstance) {
        return cryptoService.decrypt(botInstance.getBotTokenEncrypted());
    }

    public void sendInvoice(String botToken, Map<String, Object> payload) {
        post(botToken, "/sendInvoice", payload);
    }

    public void answerPreCheckoutQuery(String botToken, String preCheckoutQueryId, boolean ok, String errorMessage) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("pre_checkout_query_id", preCheckoutQueryId);
        payload.put("ok", ok);
        if (!ok) {
            payload.put("error_message", errorMessage);
        }
        post(botToken, "/answerPreCheckoutQuery", payload);
    }

    public String createSingleUseInviteLink(String botToken, String chatId, Instant expireDate) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", chatId);
        payload.put("member_limit", 1);
        payload.put("expire_date", expireDate.getEpochSecond());
        Map<?, ?> response = post(botToken, "/createChatInviteLink", payload);
        Object result = response.get("result");
        if (result instanceof Map<?, ?> resultMap) {
            Object link = resultMap.get("invite_link");
            return link == null ? null : link.toString();
        }
        return null;
    }

    public void sendMessage(String botToken, String chatId, String text) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", chatId);
        payload.put("text", text);
        post(botToken, "/sendMessage", payload);
    }

    public void removeUserFromChat(String botToken, String chatId, String userId) {
        Map<String, Object> ban = new HashMap<>();
        ban.put("chat_id", chatId);
        ban.put("user_id", userId);
        ban.put("revoke_messages", false);
        post(botToken, "/banChatMember", ban);
        Map<String, Object> unban = new HashMap<>();
        unban.put("chat_id", chatId);
        unban.put("user_id", userId);
        post(botToken, "/unbanChatMember", unban);
    }

    public void refundStarPayment(String botToken, String userId, String telegramPaymentChargeId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", userId);
        payload.put("telegram_payment_charge_id", telegramPaymentChargeId);
        post(botToken, "/refundStarPayment", payload);
    }

    private Map<?, ?> post(String botToken, String path, Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            return restTemplate.postForObject(API_URL + botToken + path, entity, Map.class);
        } catch (Exception ex) {
            log.warn("Telegram API call failed: {}", path, ex);
            return Map.of();
        }
    }
}
