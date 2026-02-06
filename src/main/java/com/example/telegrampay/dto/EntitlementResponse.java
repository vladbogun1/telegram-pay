package com.example.telegrampay.dto;

import com.example.telegrampay.domain.EntitlementStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EntitlementResponse {
    Long id;
    Long productId;
    String telegramUserId;
    String chatId;
    Instant accessUntil;
    EntitlementStatus status;
    String inviteLink;
}
