package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WalletPayConfigRequest {
    @NotBlank
    private String storeApiKey;
    @NotBlank
    private String returnUrl;
    @NotBlank
    private String failReturnUrl;
}
