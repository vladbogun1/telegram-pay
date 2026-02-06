package com.example.telegrampay.repository;

import com.example.telegrampay.domain.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByCreatorId(Long creatorId);
}
