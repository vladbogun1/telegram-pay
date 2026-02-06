package com.example.telegrampay.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BotInstanceResponse {
    Long id;
    String name;
    String webhookSecretToken;
}
