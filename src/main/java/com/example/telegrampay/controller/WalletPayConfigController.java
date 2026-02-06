package com.example.telegrampay.controller;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.domain.WalletPayConfig;
import com.example.telegrampay.dto.WalletPayConfigRequest;
import com.example.telegrampay.dto.WalletPayConfigResponse;
import com.example.telegrampay.repository.WalletPayConfigRepository;
import com.example.telegrampay.service.CreatorLookupService;
import com.example.telegrampay.service.CryptoService;
import com.example.telegrampay.service.DtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/walletpay")
@RequiredArgsConstructor
public class WalletPayConfigController {
    private final CreatorLookupService creatorLookupService;
    private final WalletPayConfigRepository walletPayConfigRepository;
    private final CryptoService cryptoService;

    @PostMapping("/config")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletPayConfigResponse upsert(@Valid @RequestBody WalletPayConfigRequest request) {
        Creator creator = creatorLookupService.currentCreator();
        WalletPayConfig config = walletPayConfigRepository.findByCreatorId(creator.getId())
            .orElseGet(WalletPayConfig::new);
        config.setCreator(creator);
        config.setStoreApiKeyEncrypted(cryptoService.encrypt(request.getStoreApiKey()));
        config.setReturnUrl(request.getReturnUrl());
        config.setFailReturnUrl(request.getFailReturnUrl());
        return DtoMapper.toWalletPayConfigResponse(walletPayConfigRepository.save(config));
    }
}
