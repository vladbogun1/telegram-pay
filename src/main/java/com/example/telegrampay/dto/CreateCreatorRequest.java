package com.example.telegrampay.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCreatorRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
