package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterChatRequest {
    @NotNull
    private Long botInstanceId;
    @NotBlank
    private String telegramChatId;
    @NotBlank
    private String title;
    @NotBlank
    private String type;
}
