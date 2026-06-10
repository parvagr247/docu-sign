package com.docu_sign.dto;

import com.docu_sign.entity.DocumentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UploadDocumentResponse(
        UUID id,
        String originalFileName,
        Long fileSize,
        DocumentStatus status,
        LocalDateTime uploadedAt
) {
}
