package com.example.telegrampay.controller;

import com.example.telegrampay.domain.EntitlementStatus;
import com.example.telegrampay.domain.Order;
import com.example.telegrampay.domain.OrderStatus;
import com.example.telegrampay.dto.DashboardSummary;
import com.example.telegrampay.repository.EntitlementRepository;
import com.example.telegrampay.repository.OrderRepository;
import com.example.telegrampay.service.CreatorLookupService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final CreatorLookupService creatorLookupService;
    private final OrderRepository orderRepository;
    private final EntitlementRepository entitlementRepository;

    public DashboardController(CreatorLookupService creatorLookupService,
                               OrderRepository orderRepository,
                               EntitlementRepository entitlementRepository) {
        this.creatorLookupService = creatorLookupService;
        this.orderRepository = orderRepository;
        this.entitlementRepository = entitlementRepository;
    }

    @GetMapping
    public DashboardSummary summary() {
        Long creatorId = creatorLookupService.currentCreator().getId();
        List<Order> orders = orderRepository.findByCreatorId(creatorId);
        long totalOrders = orders.size();
        long totalPaid = orders.stream().filter(order -> order.getStatus() == OrderStatus.PAID).count();
        long totalRevenueStars = orders.stream()
            .filter(order -> order.getStatus() == OrderStatus.PAID)
            .mapToLong(Order::getAmountStars)
            .sum();
        long activeSubscribers = entitlementRepository.findByCreatorId(creatorId).stream()
            .filter(entitlement -> entitlement.getStatus() == EntitlementStatus.ACTIVE)
            .count();
        return new DashboardSummary(totalOrders, totalPaid, activeSubscribers, totalRevenueStars);
    }
}
