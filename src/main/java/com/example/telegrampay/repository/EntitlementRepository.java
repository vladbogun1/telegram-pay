package com.example.telegrampay.repository;

import com.example.telegrampay.domain.Entitlement;
import com.example.telegrampay.domain.EntitlementStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntitlementRepository extends JpaRepository<Entitlement, Long> {
    List<Entitlement> findByStatusAndAccessUntilBefore(EntitlementStatus status, Instant cutoff);

    List<Entitlement> findByCreatorId(Long creatorId);

    List<Entitlement> findByStatusAndAccessUntilBetween(EntitlementStatus status, Instant start, Instant end);
}
