package com.example.telegrampay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WalletPayWebhook {
    private String orderId;
    private String externalId;
    private String status;
}
