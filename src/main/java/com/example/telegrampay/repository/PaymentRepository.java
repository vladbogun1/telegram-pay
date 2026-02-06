package com.example.telegrampay.repository;

import com.example.telegrampay.domain.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTelegramPaymentChargeId(String telegramPaymentChargeId);

    Optional<Payment> findTopByOrderId(Long orderId);
}
