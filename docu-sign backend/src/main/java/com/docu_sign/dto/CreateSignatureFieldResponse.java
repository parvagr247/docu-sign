package com.docu_sign.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;


@Builder
public record CreateSignatureFieldResponse(

        UUID id,
        UUID signerId,
        Integer pageNumber,
        Float xPosition,
        Float yPosition,
        Float width,
        Float height,
        Boolean required,
        Instant createdAt
) {
}
