package com.example.telegrampay.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WalletPayConfigResponse {
    Long id;
    String returnUrl;
    String failReturnUrl;
}
