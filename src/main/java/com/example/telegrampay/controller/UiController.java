package com.example.telegrampay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {
    @GetMapping("/")
    public String root() {
        return "ui";
    }

    @GetMapping("/ui")
    public String ui() {
        return "ui";
    }
}
