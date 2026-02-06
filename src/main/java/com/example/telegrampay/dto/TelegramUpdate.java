package com.example.telegrampay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TelegramUpdate {
    private Message message;
    @JsonProperty("pre_checkout_query")
    private PreCheckoutQuery preCheckoutQuery;

    @Data
    public static class Message {
        private String text;
        private User from;
        @JsonProperty("successful_payment")
        private SuccessfulPayment successfulPayment;
    }

    @Data
    public static class User {
        private Long id;
    }

    @Data
    public static class PreCheckoutQuery {
        private String id;
        private User from;
        @JsonProperty("invoice_payload")
        private String invoicePayload;
    }

    @Data
    public static class SuccessfulPayment {
        @JsonProperty("telegram_payment_charge_id")
        private String telegramPaymentChargeId;
        @JsonProperty("provider_payment_charge_id")
        private String providerPaymentChargeId;
        @JsonProperty("invoice_payload")
        private String invoicePayload;
        @JsonProperty("subscription_expiration_date")
        private Long subscriptionExpirationDate;
        @JsonProperty("is_recurring")
        private Boolean recurring;
        @JsonProperty("is_first_recurring")
        private Boolean firstRecurring;
    }
}
