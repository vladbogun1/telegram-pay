package com.example.telegrampay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProductRequest {
    @NotNull
    private Long chatId;
    @NotBlank
    private String name;
    @Min(1)
    private int priceStars;
    private Integer durationDays;
    private boolean recurringMonthly;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriceStars() {
        return priceStars;
    }

    public void setPriceStars(int priceStars) {
        this.priceStars = priceStars;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public boolean isRecurringMonthly() {
        return recurringMonthly;
    }

    public void setRecurringMonthly(boolean recurringMonthly) {
        this.recurringMonthly = recurringMonthly;
    }
}
