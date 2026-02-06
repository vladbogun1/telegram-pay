package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;

public class WalletPayConfigRequest {
    @NotBlank
    private String storeApiKey;
    @NotBlank
    private String returnUrl;
    @NotBlank
    private String failReturnUrl;

    public String getStoreApiKey() {
        return storeApiKey;
    }

    public void setStoreApiKey(String storeApiKey) {
        this.storeApiKey = storeApiKey;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getFailReturnUrl() {
        return failReturnUrl;
    }

    public void setFailReturnUrl(String failReturnUrl) {
        this.failReturnUrl = failReturnUrl;
    }
}
