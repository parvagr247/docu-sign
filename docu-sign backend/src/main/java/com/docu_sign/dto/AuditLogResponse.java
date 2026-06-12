package com.docu_sign.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AuditLogResponse(
        UUID id,
        String eventType,
        String metadata,
        String signerName,
        LocalDateTime createdAt
) {
}
