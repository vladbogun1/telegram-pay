package com.example.telegrampay.dto;

public class DashboardSummary {
    private long totalOrders;
    private long totalPaid;
    private long activeSubscribers;
    private long totalRevenueStars;

    public DashboardSummary(long totalOrders, long totalPaid, long activeSubscribers, long totalRevenueStars) {
        this.totalOrders = totalOrders;
        this.totalPaid = totalPaid;
        this.activeSubscribers = activeSubscribers;
        this.totalRevenueStars = totalRevenueStars;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public long getTotalPaid() {
        return totalPaid;
    }

    public long getActiveSubscribers() {
        return activeSubscribers;
    }

    public long getTotalRevenueStars() {
        return totalRevenueStars;
    }
}
