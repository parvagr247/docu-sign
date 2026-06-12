package com.docu_sign.service;

import com.docu_sign.entity.AuditEventType;
import com.docu_sign.entity.Document;
import com.docu_sign.entity.Signer;

public interface AuditLogService {

    void logEvent( Document document, Signer signer, AuditEventType eventType, String metadata );
}
