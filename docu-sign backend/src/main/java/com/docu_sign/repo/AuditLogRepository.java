package com.docu_sign.repo;

import com.docu_sign.entity.AuditLog;
import com.docu_sign.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByDocumentOrderByCreatedAtAsc(Document document);
}