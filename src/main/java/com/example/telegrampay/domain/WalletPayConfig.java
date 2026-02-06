package com.example.telegrampay.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallet_pay_configs")
public class WalletPayConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", unique = true)
    private Creator creator;

    @Column(nullable = false, length = 4096)
    private String storeApiKeyEncrypted;

    @Column(nullable = false)
    private String returnUrl;

    @Column(nullable = false)
    private String failReturnUrl;

    public Long getId() {
        return id;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public String getStoreApiKeyEncrypted() {
        return storeApiKeyEncrypted;
    }

    public void setStoreApiKeyEncrypted(String storeApiKeyEncrypted) {
        this.storeApiKeyEncrypted = storeApiKeyEncrypted;
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
