package com.docu_sign.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateSignatureFieldRequest(

        @NotNull
        @PositiveOrZero
        Float xPosition,

        @NotNull
        @PositiveOrZero
        Float yPosition

) {
}
