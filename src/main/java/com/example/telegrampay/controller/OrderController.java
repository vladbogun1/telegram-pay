package com.example.telegrampay.controller;

import com.example.telegrampay.domain.BotInstance;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.domain.Order;
import com.example.telegrampay.dto.CreateStarsInvoiceRequest;
import com.example.telegrampay.dto.WalletPayOrderRequest;
import com.example.telegrampay.repository.BotInstanceRepository;
import com.example.telegrampay.repository.OrderRepository;
import com.example.telegrampay.service.CreatorLookupService;
import com.example.telegrampay.service.PaymentService;
import com.example.telegrampay.service.WalletPayService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final CreatorLookupService creatorLookupService;
    private final BotInstanceRepository botInstanceRepository;
    private final PaymentService paymentService;
    private final WalletPayService walletPayService;
    private final OrderRepository orderRepository;

    public OrderController(CreatorLookupService creatorLookupService,
                           BotInstanceRepository botInstanceRepository,
                           PaymentService paymentService,
                           WalletPayService walletPayService,
                           OrderRepository orderRepository) {
        this.creatorLookupService = creatorLookupService;
        this.botInstanceRepository = botInstanceRepository;
        this.paymentService = paymentService;
        this.walletPayService = walletPayService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/stars")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createStarsInvoice(@Valid @RequestBody CreateStarsInvoiceRequest request) {
        Creator creator = creatorLookupService.currentCreator();
        BotInstance botInstance = botInstanceRepository.findById(request.getBotInstanceId())
            .filter(bot -> bot.getCreator().getId().equals(creator.getId()))
            .orElseThrow(() -> new IllegalArgumentException("Bot not found"));
        return paymentService.createStarsInvoice(creator, botInstance, request.getProductId(), request.getTelegramUserId());
    }

    @PostMapping("/walletpay")
    public Map<String, Object> createWalletPayOrder(@Valid @RequestBody WalletPayOrderRequest request) {
        Creator creator = creatorLookupService.currentCreator();
        return walletPayService.createWalletPayOrder(creator, request.getProductId(), request.getTelegramUserId());
    }

    @GetMapping
    public List<Order> listOrders() {
        Creator creator = creatorLookupService.currentCreator();
        return orderRepository.findByCreatorId(creator.getId());
    }

    @PostMapping("/{orderId}/refund")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void refund(@PathVariable Long orderId) {
        Creator creator = creatorLookupService.currentCreator();
        paymentService.refundStars(creator, orderId);
    }
}
