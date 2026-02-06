package com.example.telegrampay;

import com.example.telegrampay.config.CryptoProperties;
import com.example.telegrampay.service.CryptoService;
import java.util.Base64;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoServiceTest {
    @Test
    void encryptsAndDecrypts() {
        byte[] key = new byte[32];
        String keyBase64 = Base64.getEncoder().encodeToString(key);
        CryptoService service = new CryptoService(new CryptoProperties(keyBase64));

        String encrypted = service.encrypt("secret");
        assertThat(encrypted).isNotBlank();
        assertThat(service.decrypt(encrypted)).isEqualTo("secret");
    }
}
