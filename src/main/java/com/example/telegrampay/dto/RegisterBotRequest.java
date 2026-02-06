package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterBotRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String botToken;
}
