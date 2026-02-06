package com.example.telegrampay.service;

import com.example.telegrampay.domain.AuditLog;
import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Creator creator, String action, String details) {
        AuditLog log = new AuditLog();
        log.setCreator(creator);
        log.setAction(action);
        log.setDetails(details);
        auditLogRepository.save(log);
    }
}
