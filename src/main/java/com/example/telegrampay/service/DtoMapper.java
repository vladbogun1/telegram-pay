package com.example.telegrampay.service;

import com.example.telegrampay.domain.BotInstance;
import com.example.telegrampay.domain.Chat;
import com.example.telegrampay.domain.Entitlement;
import com.example.telegrampay.domain.Order;
import com.example.telegrampay.domain.Payment;
import com.example.telegrampay.domain.Product;
import com.example.telegrampay.domain.WalletPayConfig;
import com.example.telegrampay.dto.BotInstanceResponse;
import com.example.telegrampay.dto.ChatResponse;
import com.example.telegrampay.dto.EntitlementResponse;
import com.example.telegrampay.dto.OrderResponse;
import com.example.telegrampay.dto.PaymentResponse;
import com.example.telegrampay.dto.ProductResponse;
import com.example.telegrampay.dto.WalletPayConfigResponse;

public final class DtoMapper {
    private DtoMapper() {
    }

    public static BotInstanceResponse toBotResponse(BotInstance bot) {
        return BotInstanceResponse.builder()
            .id(bot.getId())
            .name(bot.getName())
            .webhookSecretToken(bot.getWebhookSecretToken())
            .build();
    }

    public static ChatResponse toChatResponse(Chat chat) {
        return ChatResponse.builder()
            .id(chat.getId())
            .botInstanceId(chat.getBotInstance().getId())
            .telegramChatId(chat.getTelegramChatId())
            .title(chat.getTitle())
            .type(chat.getType())
            .build();
    }

    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .chatId(product.getChat().getId())
            .name(product.getName())
            .priceStars(product.getPriceStars())
            .durationDays(product.getDurationDays())
            .recurringMonthly(product.isRecurringMonthly())
            .build();
    }

    public static OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
            .id(order.getId())
            .provider(order.getProvider())
            .status(order.getStatus())
            .telegramUserId(order.getTelegramUserId())
            .amountStars(order.getAmountStars())
            .externalId(order.getExternalId())
            .build();
    }

    public static PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
            .id(payment.getId())
            .orderId(payment.getOrder().getId())
            .provider(payment.getProvider())
            .status(payment.getStatus())
            .telegramPaymentChargeId(payment.getTelegramPaymentChargeId())
            .providerPaymentChargeId(payment.getProviderPaymentChargeId())
            .subscriptionExpirationDate(payment.getSubscriptionExpirationDate())
            .recurring(payment.getRecurring())
            .firstRecurring(payment.getFirstRecurring())
            .build();
    }

    public static EntitlementResponse toEntitlementResponse(Entitlement entitlement) {
        return EntitlementResponse.builder()
            .id(entitlement.getId())
            .productId(entitlement.getProduct().getId())
            .telegramUserId(entitlement.getTelegramUserId())
            .chatId(entitlement.getChatId())
            .accessUntil(entitlement.getAccessUntil())
            .status(entitlement.getStatus())
            .inviteLink(entitlement.getInviteLink())
            .build();
    }

    public static WalletPayConfigResponse toWalletPayConfigResponse(WalletPayConfig config) {
        return WalletPayConfigResponse.builder()
            .id(config.getId())
            .returnUrl(config.getReturnUrl())
            .failReturnUrl(config.getFailReturnUrl())
            .build();
    }
}
