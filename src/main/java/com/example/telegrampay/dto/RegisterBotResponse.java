package com.example.telegrampay.dto;

import lombok.Value;

@Value
public class RegisterBotResponse {
    Long id;
    String webhookSecretToken;
}
