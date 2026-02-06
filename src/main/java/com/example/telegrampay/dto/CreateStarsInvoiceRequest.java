package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateStarsInvoiceRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long botInstanceId;
    @NotBlank
    private String telegramUserId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBotInstanceId() {
        return botInstanceId;
    }

    public void setBotInstanceId(Long botInstanceId) {
        this.botInstanceId = botInstanceId;
    }

    public String getTelegramUserId() {
        return telegramUserId;
    }

    public void setTelegramUserId(String telegramUserId) {
        this.telegramUserId = telegramUserId;
    }
}
