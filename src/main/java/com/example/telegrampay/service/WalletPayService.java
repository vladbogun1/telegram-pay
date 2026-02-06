package com.example.telegrampay.service;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.domain.Order;
import com.example.telegrampay.domain.OrderStatus;
import com.example.telegrampay.domain.Payment;
import com.example.telegrampay.domain.PaymentProvider;
import com.example.telegrampay.domain.PaymentStatus;
import com.example.telegrampay.domain.Product;
import com.example.telegrampay.domain.WalletPayConfig;
import com.example.telegrampay.dto.WalletPayWebhook;
import com.example.telegrampay.repository.OrderRepository;
import com.example.telegrampay.repository.PaymentRepository;
import com.example.telegrampay.repository.ProductRepository;
import com.example.telegrampay.repository.WalletPayConfigRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WalletPayService {
    private static final String WALLET_PAY_URL = "https://pay.wallet.tg/wpay/store-api/v1/order";

    private final WalletPayConfigRepository walletPayConfigRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final EntitlementService entitlementService;
    private final TelegramService telegramService;
    private final CryptoService cryptoService;
    private final RestTemplate restTemplate;
    private final AuditLogService auditLogService;

    public WalletPayService(WalletPayConfigRepository walletPayConfigRepository,
                            ProductRepository productRepository,
                            OrderRepository orderRepository,
                            PaymentRepository paymentRepository,
                            EntitlementService entitlementService,
                            TelegramService telegramService,
                            CryptoService cryptoService,
                            RestTemplate restTemplate,
                            AuditLogService auditLogService) {
        this.walletPayConfigRepository = walletPayConfigRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.entitlementService = entitlementService;
        this.telegramService = telegramService;
        this.cryptoService = cryptoService;
        this.restTemplate = restTemplate;
        this.auditLogService = auditLogService;
    }

    public Map<String, Object> createWalletPayOrder(Creator creator, Long productId, String telegramUserId) {
        WalletPayConfig config = walletPayConfigRepository.findByCreatorId(creator.getId())
            .orElseThrow(() -> new IllegalStateException("Wallet Pay config missing"));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (!product.getCreator().getId().equals(creator.getId())) {
            throw new IllegalArgumentException("Product not found");
        }

        Order order = new Order();
        order.setCreator(creator);
        order.setProduct(product);
        order.setProvider(PaymentProvider.WALLET_PAY);
        order.setTelegramUserId(telegramUserId);
        order.setAmountStars(product.getPriceStars());
        order.setExternalId(UUID.randomUUID().toString());
        order = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(PaymentProvider.WALLET_PAY);
        paymentRepository.save(payment);

        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", product.getPriceStars());
        payload.put("currency", "XTR");
        payload.put("externalId", order.getExternalId());
        payload.put("description", "Access to " + product.getChat().getTitle());
        payload.put("returnUrl", config.getReturnUrl());
        payload.put("failReturnUrl", config.getFailReturnUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Wpay-Store-Api-Key", cryptoService.decrypt(config.getStoreApiKeyEncrypted()));
        Map<String, Object> response = restTemplate.postForObject(WALLET_PAY_URL, new HttpEntity<>(payload, headers), Map.class);
        auditLogService.log(creator, "walletpay.order", "externalId=" + order.getExternalId());
        return response == null ? Map.of("externalId", order.getExternalId()) : response;
    }

    public void handleWalletPayWebhook(Creator creator, WalletPayWebhook webhook) {
        Optional<Order> existing = orderRepository.findByExternalId(webhook.getExternalId());
        if (existing.isEmpty()) {
            return;
        }
        Order order = existing.get();
        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }
        if (!"PAID".equalsIgnoreCase(webhook.getStatus())) {
            return;
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(PaymentProvider.WALLET_PAY);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProviderPaymentChargeId(webhook.getOrderId());
        paymentRepository.save(payment);

        String botToken = telegramService.resolveBotToken(order.getProduct().getChat().getBotInstance());
        Instant accessUntil = Instant.now().plusSeconds(30L * 24 * 60 * 60);
        if (order.getProduct().getDurationDays() != null) {
            accessUntil = Instant.now().plusSeconds(order.getProduct().getDurationDays() * 86400L);
        }
        entitlementService.grantAccess(creator, order.getProduct(), order.getTelegramUserId(), accessUntil, botToken);
        auditLogService.log(creator, "walletpay.paid", "externalId=" + order.getExternalId());
    }
}
