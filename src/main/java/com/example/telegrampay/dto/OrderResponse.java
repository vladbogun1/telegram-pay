package com.example.telegrampay.dto;

import com.example.telegrampay.domain.OrderStatus;
import com.example.telegrampay.domain.PaymentProvider;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderResponse {
    Long id;
    PaymentProvider provider;
    OrderStatus status;
    String telegramUserId;
    Integer amountStars;
    String externalId;
}
