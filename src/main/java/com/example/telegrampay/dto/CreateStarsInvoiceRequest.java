package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateStarsInvoiceRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Long botInstanceId;
    @NotBlank
    private String telegramUserId;
}
