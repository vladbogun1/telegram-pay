package com.example.telegrampay.dto;

import com.example.telegrampay.domain.PaymentProvider;
import com.example.telegrampay.domain.PaymentStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaymentResponse {
    Long id;
    Long orderId;
    PaymentProvider provider;
    PaymentStatus status;
    String telegramPaymentChargeId;
    String providerPaymentChargeId;
    Instant subscriptionExpirationDate;
    Boolean recurring;
    Boolean firstRecurring;
}
