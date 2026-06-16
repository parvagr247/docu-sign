package com.docu_sign.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FieldCompletionResponse(
        UUID fieldId,

        boolean completed,

        LocalDateTime completedAt
) {
}
