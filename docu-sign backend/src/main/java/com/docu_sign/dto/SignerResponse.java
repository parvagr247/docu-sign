package com.docu_sign.dto;

import com.docu_sign.entity.SignerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SignerResponse(
        UUID id,

        String name,

        String email,

        SignerStatus status,

        LocalDateTime createdAt
) {
}
