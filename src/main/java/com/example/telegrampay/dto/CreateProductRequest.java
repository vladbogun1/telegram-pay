package com.example.telegrampay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateProductRequest {
    @NotNull
    private Long chatId;
    @NotBlank
    private String name;
    @Min(1)
    private int priceStars;
    private Integer durationDays;
    private boolean recurringMonthly;
}
