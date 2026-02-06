package com.example.telegrampay.service;

import com.example.telegrampay.domain.BotInstance;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.domain.Order;
import com.example.telegrampay.domain.OrderStatus;
import com.example.telegrampay.domain.Payment;
import com.example.telegrampay.domain.PaymentProvider;
import com.example.telegrampay.domain.PaymentStatus;
import com.example.telegrampay.domain.Product;
import com.example.telegrampay.dto.TelegramUpdate;
import com.example.telegrampay.repository.OrderRepository;
import com.example.telegrampay.repository.PaymentRepository;
import com.example.telegrampay.repository.ProductRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final EntitlementService entitlementService;
    private final TelegramService telegramService;
    private final AuditLogService auditLogService;

    @Transactional
    public Order createStarsInvoice(Creator creator, BotInstance botInstance, Long productId, String telegramUserId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (!product.getCreator().getId().equals(creator.getId())) {
            throw new IllegalArgumentException("Product not found");
        }
        Order order = new Order();
        order.setCreator(creator);
        order.setProduct(product);
        order.setProvider(PaymentProvider.STARS);
        order.setTelegramUserId(telegramUserId);
        order.setAmountStars(product.getPriceStars());
        order = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(PaymentProvider.STARS);
        paymentRepository.save(payment);

        String botToken = telegramService.resolveBotToken(botInstance);
        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", telegramUserId);
        payload.put("title", product.getName());
        payload.put("description", "Access to " + product.getChat().getTitle());
        payload.put("payload", order.getId().toString());
        payload.put("provider_token", "");
        payload.put("currency", "XTR");
        payload.put("prices", new Object[] { Map.of("label", product.getName(), "amount", product.getPriceStars()) });
        if (product.isRecurringMonthly()) {
            payload.put("subscription_period", 30 * 24 * 60 * 60);
        }
        telegramService.sendInvoice(botToken, payload);
        auditLogService.log(creator, "invoice.sent", "orderId=" + order.getId());
        return order;
    }

    @Transactional
    public void handlePreCheckout(BotInstance botInstance, TelegramUpdate.PreCheckoutQuery preCheckoutQuery) {
        String botToken = telegramService.resolveBotToken(botInstance);
        try {
            Optional<Order> order = orderRepository.findById(Long.parseLong(preCheckoutQuery.getInvoicePayload()));
            if (order.isPresent()) {
                telegramService.answerPreCheckoutQuery(botToken, preCheckoutQuery.getId(), true, null);
            } else {
                telegramService.answerPreCheckoutQuery(botToken, preCheckoutQuery.getId(), false, "Order not found");
            }
        } catch (NumberFormatException ex) {
            telegramService.answerPreCheckoutQuery(botToken, preCheckoutQuery.getId(), false, "Invalid order");
        }
    }

    @Transactional
    public void handleSuccessfulPayment(BotInstance botInstance, Creator creator, TelegramUpdate.SuccessfulPayment paymentInfo, String telegramUserId) {
        if (paymentRepository.findByTelegramPaymentChargeId(paymentInfo.getTelegramPaymentChargeId()).isPresent()) {
            return;
        }
        Long orderId;
        try {
            orderId = Long.parseLong(paymentInfo.getInvoicePayload());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid order payload");
        }
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(PaymentProvider.STARS);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTelegramPaymentChargeId(paymentInfo.getTelegramPaymentChargeId());
        payment.setProviderPaymentChargeId(paymentInfo.getProviderPaymentChargeId());
        if (paymentInfo.getSubscriptionExpirationDate() != null) {
            payment.setSubscriptionExpirationDate(Instant.ofEpochSecond(paymentInfo.getSubscriptionExpirationDate()));
        }
        payment.setRecurring(paymentInfo.getRecurring());
        payment.setFirstRecurring(paymentInfo.getFirstRecurring());
        paymentRepository.save(payment);

        Instant accessUntil = resolveAccessUntil(order.getProduct(), payment);
        String botToken = telegramService.resolveBotToken(botInstance);
        entitlementService.grantAccess(creator, order.getProduct(), telegramUserId, accessUntil, botToken);
        auditLogService.log(creator, "payment.success", "orderId=" + order.getId());
    }

    public void handlePaySupport(Creator creator, BotInstance botInstance, String telegramUserId) {
        String botToken = telegramService.resolveBotToken(botInstance);
        Optional<Order> lastOrder = orderRepository.findTopByCreatorIdAndTelegramUserIdOrderByCreatedAtDesc(creator.getId(), telegramUserId);
        String status = lastOrder.map(order -> order.getStatus().name()).orElse("NO_ORDERS");
        telegramService.sendMessage(botToken, telegramUserId, "Support: support@telegram-pay.example\nLast order status: " + status);
    }

    @Transactional
    public void refundStars(Creator creator, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getCreator().getId().equals(creator.getId())) {
            throw new IllegalArgumentException("Order not found");
        }
        if (order.getProvider() != PaymentProvider.STARS) {
            throw new IllegalArgumentException("Refunds are only supported for Stars payments");
        }
        Payment payment = paymentRepository.findTopByOrderId(order.getId())
            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        if (payment.getTelegramPaymentChargeId() == null) {
            throw new IllegalArgumentException("Missing Telegram payment charge id");
        }
        String botToken = telegramService.resolveBotToken(order.getProduct().getChat().getBotInstance());
        telegramService.refundStarPayment(botToken, order.getTelegramUserId(), payment.getTelegramPaymentChargeId());
        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);
        auditLogService.log(creator, "payment.refund", "orderId=" + order.getId());
    }

    private Instant resolveAccessUntil(Product product, Payment payment) {
        if (payment.getSubscriptionExpirationDate() != null) {
            return payment.getSubscriptionExpirationDate();
        }
        if (product.getDurationDays() != null) {
            return Instant.now().plus(product.getDurationDays(), ChronoUnit.DAYS);
        }
        if (product.isRecurringMonthly()) {
            return Instant.now().plus(30, ChronoUnit.DAYS);
        }
        return Instant.now().plus(30, ChronoUnit.DAYS);
    }
}
