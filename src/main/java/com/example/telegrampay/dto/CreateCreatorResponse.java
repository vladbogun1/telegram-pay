package com.example.telegrampay.dto;

public class CreateCreatorResponse {
    private Long id;
    private String apiKey;

    public CreateCreatorResponse(Long id, String apiKey) {
        this.id = id;
        this.apiKey = apiKey;
    }

    public Long getId() {
        return id;
    }

    public String getApiKey() {
        return apiKey;
    }
}
