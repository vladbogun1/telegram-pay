package com.example.telegrampay.repository;

import com.example.telegrampay.domain.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCreatorId(Long creatorId);
}
