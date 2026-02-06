package com.example.telegrampay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelegramUpdate {
    private Message message;
    @JsonProperty("pre_checkout_query")
    private PreCheckoutQuery preCheckoutQuery;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public PreCheckoutQuery getPreCheckoutQuery() {
        return preCheckoutQuery;
    }

    public void setPreCheckoutQuery(PreCheckoutQuery preCheckoutQuery) {
        this.preCheckoutQuery = preCheckoutQuery;
    }

    public static class Message {
        private String text;
        private User from;
        @JsonProperty("successful_payment")
        private SuccessfulPayment successfulPayment;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public User getFrom() {
            return from;
        }

        public void setFrom(User from) {
            this.from = from;
        }

        public SuccessfulPayment getSuccessfulPayment() {
            return successfulPayment;
        }

        public void setSuccessfulPayment(SuccessfulPayment successfulPayment) {
            this.successfulPayment = successfulPayment;
        }
    }

    public static class User {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public static class PreCheckoutQuery {
        private String id;
        private User from;
        @JsonProperty("invoice_payload")
        private String invoicePayload;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public User getFrom() {
            return from;
        }

        public void setFrom(User from) {
            this.from = from;
        }

        public String getInvoicePayload() {
            return invoicePayload;
        }

        public void setInvoicePayload(String invoicePayload) {
            this.invoicePayload = invoicePayload;
        }
    }

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

        public String getTelegramPaymentChargeId() {
            return telegramPaymentChargeId;
        }

        public void setTelegramPaymentChargeId(String telegramPaymentChargeId) {
            this.telegramPaymentChargeId = telegramPaymentChargeId;
        }

        public String getProviderPaymentChargeId() {
            return providerPaymentChargeId;
        }

        public void setProviderPaymentChargeId(String providerPaymentChargeId) {
            this.providerPaymentChargeId = providerPaymentChargeId;
        }

        public String getInvoicePayload() {
            return invoicePayload;
        }

        public void setInvoicePayload(String invoicePayload) {
            this.invoicePayload = invoicePayload;
        }

        public Long getSubscriptionExpirationDate() {
            return subscriptionExpirationDate;
        }

        public void setSubscriptionExpirationDate(Long subscriptionExpirationDate) {
            this.subscriptionExpirationDate = subscriptionExpirationDate;
        }

        public Boolean getRecurring() {
            return recurring;
        }

        public void setRecurring(Boolean recurring) {
            this.recurring = recurring;
        }

        public Boolean getFirstRecurring() {
            return firstRecurring;
        }

        public void setFirstRecurring(Boolean firstRecurring) {
            this.firstRecurring = firstRecurring;
        }
    }
}
