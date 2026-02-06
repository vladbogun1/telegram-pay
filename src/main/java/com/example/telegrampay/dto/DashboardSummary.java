package com.example.telegrampay.dto;

import lombok.Value;

@Value
public class DashboardSummary {
    long totalOrders;
    long totalPaid;
    long activeSubscribers;
    long totalRevenueStars;
}
