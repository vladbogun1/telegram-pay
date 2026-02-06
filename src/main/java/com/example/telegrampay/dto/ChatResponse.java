package com.example.telegrampay.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatResponse {
    Long id;
    Long botInstanceId;
    String telegramChatId;
    String title;
    String type;
}
