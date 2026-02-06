package com.example.telegrampay.controller;

import com.example.telegrampay.dto.EntitlementResponse;
import com.example.telegrampay.dto.PaymentResponse;
import com.example.telegrampay.repository.EntitlementRepository;
import com.example.telegrampay.repository.PaymentRepository;
import com.example.telegrampay.service.CreatorLookupService;
import com.example.telegrampay.service.DtoMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {
    private final CreatorLookupService creatorLookupService;
    private final PaymentRepository paymentRepository;
    private final EntitlementRepository entitlementRepository;

    @GetMapping("/payments")
    public List<PaymentResponse> payments() {
        Long creatorId = creatorLookupService.currentCreator().getId();
        return paymentRepository.findAll().stream()
            .filter(payment -> payment.getOrder().getCreator().getId().equals(creatorId))
            .map(DtoMapper::toPaymentResponse)
            .toList();
    }

    @GetMapping("/entitlements")
    public List<EntitlementResponse> entitlements() {
        Long creatorId = creatorLookupService.currentCreator().getId();
        return entitlementRepository.findByCreatorId(creatorId).stream()
            .map(DtoMapper::toEntitlementResponse)
            .toList();
    }
}
