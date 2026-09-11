package com.ennaslennas.audit.service;

import com.ennaslennas.audit.domain.AuditLog;
import com.ennaslennas.audit.repository.AuditLogRepository;
import com.ennaslennas.security.domain.AdminUser;
import com.ennaslennas.security.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AdminUserRepository adminUserRepository;

    @Transactional
    public void logAction(String adminEmail, String action, String entityType, Long entityId, String details) {
        AdminUser admin = null;
        if (adminEmail != null) {
            admin = adminUserRepository.findByEmail(adminEmail).orElse(null);
        }

        AuditLog audit = AuditLog.builder()
                .adminUser(admin)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();

        auditLogRepository.save(audit);
        log.info("Audit: user={}, action={}, entity={}/{}, details={}",
                adminEmail, action, entityType, entityId, details);
    }
}
