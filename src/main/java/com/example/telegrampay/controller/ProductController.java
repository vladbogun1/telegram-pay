package com.example.telegrampay.controller;

import com.example.telegrampay.domain.Chat;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.dto.CreateProductRequest;
import com.example.telegrampay.dto.ProductResponse;
import com.example.telegrampay.repository.ChatRepository;
import com.example.telegrampay.service.CreatorLookupService;
import com.example.telegrampay.service.DtoMapper;
import com.example.telegrampay.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CreatorLookupService creatorLookupService;
    private final ChatRepository chatRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        Creator creator = creatorLookupService.currentCreator();
        Chat chat = chatRepository.findById(request.getChatId())
            .filter(found -> found.getCreator().getId().equals(creator.getId()))
            .orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        return DtoMapper.toProductResponse(
            productService.createProduct(creator, chat, request.getName(), request.getPriceStars(),
                request.getDurationDays(), request.isRecurringMonthly())
        );
    }

    @GetMapping
    public List<ProductResponse> list() {
        Creator creator = creatorLookupService.currentCreator();
        return productService.listProducts(creator.getId()).stream()
            .map(DtoMapper::toProductResponse)
            .toList();
    }
}
