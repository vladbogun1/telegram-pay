package com.example.telegrampay.controller;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.dto.WalletPayWebhook;
import com.example.telegrampay.repository.CreatorRepository;
import com.example.telegrampay.service.AuditLogService;
import com.example.telegrampay.service.WalletPayService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/walletpay")
public class WalletPayWebhookController {
    private final WalletPayService walletPayService;
    private final CreatorRepository creatorRepository;
    private final AuditLogService auditLogService;

    public WalletPayWebhookController(WalletPayService walletPayService,
                                      CreatorRepository creatorRepository,
                                      AuditLogService auditLogService) {
        this.walletPayService = walletPayService;
        this.creatorRepository = creatorRepository;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/{creatorId}")
    @ResponseStatus(HttpStatus.OK)
    public void receive(@PathVariable Long creatorId, @RequestBody WalletPayWebhook webhook) {
        Creator creator = creatorRepository.findById(creatorId)
            .orElseThrow(() -> new IllegalArgumentException("Creator not found"));
        auditLogService.log(creator, "webhook.walletpay", "externalId=" + webhook.getExternalId());
        walletPayService.handleWalletPayWebhook(creator, webhook);
    }
}
