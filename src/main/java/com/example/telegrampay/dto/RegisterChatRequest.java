package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterChatRequest {
    @NotNull
    private Long botInstanceId;
    @NotBlank
    private String telegramChatId;
    @NotBlank
    private String title;
    @NotBlank
    private String type;

    public Long getBotInstanceId() {
        return botInstanceId;
    }

    public void setBotInstanceId(Long botInstanceId) {
        this.botInstanceId = botInstanceId;
    }

    public String getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
