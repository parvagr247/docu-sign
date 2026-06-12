package com.docu_sign.service;

import com.docu_sign.dto.AuditLogResponse;
import com.docu_sign.entity.AuditEventType;
import com.docu_sign.entity.Document;
import com.docu_sign.entity.Signer;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {

    void logEvent( Document document, Signer signer, AuditEventType eventType, String metadata );

    List<AuditLogResponse> getDocumentAuditHistory(UUID documentId);
}
