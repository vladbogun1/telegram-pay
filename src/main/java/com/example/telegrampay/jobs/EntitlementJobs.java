package com.example.telegrampay.jobs;

import com.example.telegrampay.service.EntitlementService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EntitlementJobs {
    private final EntitlementService entitlementService;

    public EntitlementJobs(EntitlementService entitlementService) {
        this.entitlementService = entitlementService;
    }

    @Scheduled(fixedDelayString = "${telegram-pay.jobs.expire-delay-ms:60000}")
    public void expireEntitlements() {
        entitlementService.expireEntitlements();
    }

    @Scheduled(cron = "${telegram-pay.jobs.reminder-cron:0 0 * * * *}")
    public void sendReminders() {
        entitlementService.sendRenewalReminders();
    }
}
