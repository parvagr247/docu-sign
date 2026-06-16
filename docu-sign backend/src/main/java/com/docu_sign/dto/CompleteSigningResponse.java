package com.docu_sign.dto;

import com.docu_sign.entity.DocumentStatus;
import com.docu_sign.entity.SignerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompleteSigningResponse(
        UUID signerId,

        SignerStatus signerStatus,

        DocumentStatus documentStatus,

        LocalDateTime signedAt,

        String message

) {
}
