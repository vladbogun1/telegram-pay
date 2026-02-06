package com.example.telegrampay.repository;

import com.example.telegrampay.domain.WalletPayConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletPayConfigRepository extends JpaRepository<WalletPayConfig, Long> {
    Optional<WalletPayConfig> findByCreatorId(Long creatorId);
}
