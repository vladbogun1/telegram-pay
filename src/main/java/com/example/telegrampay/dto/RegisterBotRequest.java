package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterBotRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String botToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
