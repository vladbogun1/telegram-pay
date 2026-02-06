package com.example.telegrampay.controller;

import com.example.telegrampay.domain.Entitlement;
import com.example.telegrampay.domain.Payment;
import com.example.telegrampay.repository.EntitlementRepository;
import com.example.telegrampay.repository.PaymentRepository;
import com.example.telegrampay.service.CreatorLookupService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reporting")
public class ReportingController {
    private final CreatorLookupService creatorLookupService;
    private final PaymentRepository paymentRepository;
    private final EntitlementRepository entitlementRepository;

    public ReportingController(CreatorLookupService creatorLookupService,
                               PaymentRepository paymentRepository,
                               EntitlementRepository entitlementRepository) {
        this.creatorLookupService = creatorLookupService;
        this.paymentRepository = paymentRepository;
        this.entitlementRepository = entitlementRepository;
    }

    @GetMapping("/payments")
    public List<Payment> payments() {
        Long creatorId = creatorLookupService.currentCreator().getId();
        return paymentRepository.findAll().stream()
            .filter(payment -> payment.getOrder().getCreator().getId().equals(creatorId))
            .toList();
    }

    @GetMapping("/entitlements")
    public List<Entitlement> entitlements() {
        Long creatorId = creatorLookupService.currentCreator().getId();
        return entitlementRepository.findByCreatorId(creatorId);
    }
}
