package com.docu_sign.dto;

import java.util.UUID;

public record SignatureFieldViewResponse(
        UUID id,

        Integer pageNumber,

        Float xPosition,

        Float yPosition,

        Float width,

        Float height,

        Boolean required,

        Boolean completed
) {
}
