package com.example.telegrampay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram-pay.crypto")
public record CryptoProperties(String masterKeyBase64) {
}
