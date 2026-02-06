package com.example.telegrampay;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.repository.CreatorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class RepositoryIntegrationTest {
    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
        .withDatabaseName("telegram_pay")
        .withUsername("telegram")
        .withPassword("telegram");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("telegram-pay.crypto.master-key-base64", () -> "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
    }

    @Autowired
    private CreatorRepository creatorRepository;

    @Test
    void savesCreator() {
        Creator creator = new Creator();
        creator.setName("Test");
        creator.setApiKey("key");
        Creator saved = creatorRepository.save(creator);
        assertThat(saved.getId()).isNotNull();
    }
}
