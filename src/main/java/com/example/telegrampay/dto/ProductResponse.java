package com.example.telegrampay.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductResponse {
    Long id;
    Long chatId;
    String name;
    Integer priceStars;
    Integer durationDays;
    boolean recurringMonthly;
}
