package com.example.telegrampay.controller;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.domain.WalletPayConfig;
import com.example.telegrampay.dto.WalletPayConfigRequest;
import com.example.telegrampay.repository.WalletPayConfigRepository;
import com.example.telegrampay.service.CreatorLookupService;
import com.example.telegrampay.service.CryptoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/walletpay")
public class WalletPayConfigController {
    private final CreatorLookupService creatorLookupService;
    private final WalletPayConfigRepository walletPayConfigRepository;
    private final CryptoService cryptoService;

    public WalletPayConfigController(CreatorLookupService creatorLookupService,
                                     WalletPayConfigRepository walletPayConfigRepository,
                                     CryptoService cryptoService) {
        this.creatorLookupService = creatorLookupService;
        this.walletPayConfigRepository = walletPayConfigRepository;
        this.cryptoService = cryptoService;
    }

    @PostMapping("/config")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletPayConfig upsert(@Valid @RequestBody WalletPayConfigRequest request) {
        Creator creator = creatorLookupService.currentCreator();
        WalletPayConfig config = walletPayConfigRepository.findByCreatorId(creator.getId())
            .orElseGet(WalletPayConfig::new);
        config.setCreator(creator);
        config.setStoreApiKeyEncrypted(cryptoService.encrypt(request.getStoreApiKey()));
        config.setReturnUrl(request.getReturnUrl());
        config.setFailReturnUrl(request.getFailReturnUrl());
        return walletPayConfigRepository.save(config);
    }
}
