package com.docu_sign.dto;

import com.docu_sign.entity.DocumentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SendSignatureResponse(
        UUID documentId,
        DocumentStatus status,
        LocalDateTime signatureRequestedAt
) {
}