package com.example.telegrampay.dto;

public class RegisterBotResponse {
    private Long id;
    private String webhookSecretToken;

    public RegisterBotResponse(Long id, String webhookSecretToken) {
        this.id = id;
        this.webhookSecretToken = webhookSecretToken;
    }

    public Long getId() {
        return id;
    }

    public String getWebhookSecretToken() {
        return webhookSecretToken;
    }
}
