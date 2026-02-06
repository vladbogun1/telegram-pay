package com.example.telegrampay.service;

import com.example.telegrampay.domain.Chat;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.domain.Product;
import com.example.telegrampay.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product createProduct(Creator creator, Chat chat, String name, int priceStars, Integer durationDays, boolean recurringMonthly) {
        Product product = new Product();
        product.setCreator(creator);
        product.setChat(chat);
        product.setName(name);
        product.setPriceStars(priceStars);
        product.setDurationDays(durationDays);
        product.setRecurringMonthly(recurringMonthly);
        return productRepository.save(product);
    }

    public List<Product> listProducts(Long creatorId) {
        return productRepository.findByCreatorId(creatorId);
    }
}
