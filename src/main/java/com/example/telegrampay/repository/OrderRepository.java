package com.example.telegrampay.repository;

import com.example.telegrampay.domain.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCreatorId(Long creatorId);

    Optional<Order> findByExternalId(String externalId);

    Optional<Order> findTopByCreatorIdAndTelegramUserIdOrderByCreatedAtDesc(Long creatorId, String telegramUserId);
}
